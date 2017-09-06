package com.alexo;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
    }

}
