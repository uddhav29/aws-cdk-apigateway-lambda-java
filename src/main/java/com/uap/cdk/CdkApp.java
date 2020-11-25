package com.uap.cdk;

import software.amazon.awscdk.core.App;

public final class CdkApp {
    public static void main(final String[] args) throws Exception {
        App app = new App();

        new CdkStack(app, "cdkStack");

        app.synth();
    }
}
