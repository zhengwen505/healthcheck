package sys.base.health.check.resource;

import javax.sql.DataSource;

public class DbResource implements Resource {
    private DataSource dataSource;

    public Object getMonitorEntity() {
        return getDataSource();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DbResource(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    public DbResource() {
        super();
    }
    

}
