package sys.base.health.check;

public interface Check {

    void check();

    /**
     * is safe
     */
    boolean isSafe();

    Object afterCheckSuccess();

    Object afterCheckFail();

}
