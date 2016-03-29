package sys.base.health.check;

import sys.base.health.check.resource.Resource;

public interface ResourceCheck extends Check {

    Resource getResource();

}
