package process;

public interface ISubtaskIterator {
    boolean hasNext();
    Subtask next();
    void submit(Subtask subtask);
    void exception(Subtask subtask,Exception e);
}
