package sys.base.health.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sys.base.health.check.resource.Resource;

public abstract class AbstractResourceCheck implements ResourceCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResourceCheck.class);
    private Resource resource;

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void check() {
        try {
            if (isSafe()) {
                LOGGER.info("resource health check fail");
                afterCheckSuccess();
            } else {
                afterCheckFail();
            }
        } catch (Exception e) {
            LOGGER.error("resource health check fail", e);
            afterCheckFail();
        }

    }

    protected void handleDifferentResourceWhenGetResourceFailIfYouWantSomeSpecificRequest(int tryCount, Exception e) {
    }

    public Resource getResource() {
        return this.resource;
    }

    /**
     * current thread sleep
     * 
     * @param time
     */
    public void currentThreadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            LOGGER.error("heartbeat thread sleep fail", e);
        }
    }

}
