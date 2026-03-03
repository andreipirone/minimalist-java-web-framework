package com.simple.framework;

import java.io.IOException;

public interface Handler {
    void execute(Request req, Response res) throws IOException;
}
