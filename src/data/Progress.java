package data;

import process.Subtask;

public class Progress {
    public Settings settings;
    public int addressIndex;
    public int portIndex;

    public long startTime;

    public static final int STATUS_INPROGRESS=0,STATUS_FINISHED=1;
    public int status=0;
    public Subtask[] results;
}
