package result;

public class FailureOrResult<T extends Record> {
    private T result;
    private FailureResult failure;

    public FailureOrResult(T result){
        assert(!(result instanceof FailureResult));
        this.result = result;
    }

    public FailureOrResult(FailureResult failure){
        this.failure = failure;
    }
    
    public boolean wasSuccessful(){
        return result != null;
    }

    public T getResult(){
        return result;
    }

    public FailureResult getFailure(){
        return failure;
    }

}
