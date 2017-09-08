package com.alexo;

import com.alexo.jdbi.PostgresDAO;
import com.alexo.jdbi.ReadDAO;
import com.alexo.resources.TestResource;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class PostgresSQLApplication extends Application<PostgresSQLConfiguration> {

    public static void main(final String[] args) throws Exception {
        new PostgresSQLApplication().run(args);
    }

    @Override
    public String getName() {
        return "PostgresSQL";
    }

    @Override
    public void initialize(final Bootstrap<PostgresSQLConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final PostgresSQLConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

        // final UserDAO dao = jdbi.onDemand(UserDAO.class);
        // environment.jersey().register(new UserResource(dao));
        final PostgresDAO postgresDAO = jdbi.onDemand(PostgresDAO.class);
        final ReadDAO readDAO = jdbi.onDemand(ReadDAO.class);

        final TestResource testResource = new TestResource(postgresDAO, readDAO);
        environment.jersey().register(testResource);
    }

}
