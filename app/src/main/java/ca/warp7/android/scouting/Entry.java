package ca.warp7.android.scouting;

import java.util.ArrayList;

/**
 * Data model for the scouting app. Strictly, it follows
 * a per-match-per-board model, but it can be mostly
 * taken as per-match-per-team. Each instance contains
 * a stack of data recorded for that particular entry,
 * which can be classified into two groups: 1) data points
 * of a singular magnitude, such as the robot's starting
 * position and its subjective driving speed; in such cases,
 * it is usually desirable to know what the last-most value
 * set to them; and 2) the time series in which each data
 * point records the occurrence of a particular action at
 * a specific time, and there may be multiple values of
 * this type that are of equal interest to the data collector,
 * such as recording whenever a game piece is picked up
 * by the robot; in these cases, it may be helpful to know
 * the the count, the parity(Start/End), duration between each
 * occurrence, or a combination of the above. In stack of data
 * are recorded according to their time of input as referenced
 * by the time tracked by the scouting interface; this means
 * that even if a data point does not track time, the stack
 * should preserve the order of input nonetheless. For the
 * purpose of usage and analysis, the implementation using
 * this class should limit one data point per second.
 *
 * @author Team 865
 * @see EntryDatum
 * @see EntryFormatter
 */

@SuppressWarnings("unused")
class Entry {

    private int mMatchNumber;
    private int mTeamNumber;
    private String mScoutName;

    private int mStartingTimestamp;

    private Specs mSpecs;

    private ArrayList<EntryDatum> mDataStack;

    private Timekeeper mTimekeeper;

    Entry(int matchNumber,
          int teamNumber,
          String scoutName,
          Timekeeper timekeeper) {

        mMatchNumber = matchNumber;
        mTeamNumber = teamNumber;
        this.mScoutName = scoutName;

        mStartingTimestamp = (int) (System.currentTimeMillis() / 1000);

        mSpecs = Specs.getInstance();

        mDataStack = new ArrayList<>();

        mTimekeeper = timekeeper;
    }

    public int getMatchNumber() {
        return mMatchNumber;
    }

    public int getTeamNumber() {
        return mTeamNumber;
    }

    public String getScoutName() {
        return mScoutName;
    }

    public int getStartingTimestamp() {
        return mStartingTimestamp;
    }

    public Specs getSpecs() {
        return mSpecs;
    }

    public ArrayList<EntryDatum> getDataStack() {
        return mDataStack;
    }

    public void setStartingTimestamp(int timestamp) {
        mStartingTimestamp = timestamp;
    }

    /**
     * Pushes some data into the data stack
     */

    void push(int dataType, int dataValue, int dataState) {

        if (dataType < 0 || dataType > 63) {
            return;
        }

        EntryDatum datum = new EntryDatum(dataType, dataValue,
                mTimekeeper.getCurrentRelativeTime());

        datum.setStateFlag(dataState);

        mDataStack.add(maxIndexBeforeCurrentTime(), datum);
    }

    /**
     * Performs an undo action on the data stack
     *
     * @return the data constant(metrics) of the datum being undone, or null
     * if nothing can be undone
     */

    Specs.DataConstant undo() {

        for (int i = mDataStack.size() - 1; i >= 0; i--) {

            EntryDatum datum = mDataStack.get(i);

            if (datum.getUndoFlag() == 0) {
                datum.flagAsUndone();
                return mSpecs.getDataConstantByIndex(datum.getType());
            }
        }

        return null;
    }

    /**
     * Gets the count of a specific data type, excluding undo
     */

    int getCount(int dataType) {

        int total = 0;

        for (int i = maxIndexBeforeCurrentTime() - 1; i >= 0; i--) {
            EntryDatum datum = mDataStack.get(i);
            if (datum.getType() == dataType && datum.getUndoFlag() == 0) {
                total++;
            }
        }

        return total;
    }

    /**
     * Gets the last recorded of a specific data type, excluding undo
     */

    int getLastValue(int dataType) {

        for (int i = maxIndexBeforeCurrentTime() - 1; i >= 0; i--) {
            EntryDatum datum = mDataStack.get(i);
            if (datum.getType() == dataType && datum.getUndoFlag() == 0) {
                return datum.getValue();
            }
        }
        return 0;
    }

    /**
     * Cleans out data that have been undone
     */

    void clean() {

        ArrayList<EntryDatum> cleanedList = new ArrayList<>();

        for (EntryDatum datum : mDataStack) {
            if (datum.getUndoFlag() == 0) {
                cleanedList.add(datum);
            }
        }

        mDataStack = cleanedList;
    }

    /**
     * Get the maximum index of the datum recorded before or equal the current time,
     * or the last item in the data stack
     */
    private int maxIndexBeforeCurrentTime() {

        int index = 0; // find the maximum index that is less than current time
        while (index < mDataStack.size() &&
                mDataStack.get(index).getRecordedTime() <= mTimekeeper.getCurrentRelativeTime()) {
            index++;
        }
        return index;

    }

    interface Timekeeper {
        @SuppressWarnings("unused")
        int getCurrentRelativeTime();
    }
}