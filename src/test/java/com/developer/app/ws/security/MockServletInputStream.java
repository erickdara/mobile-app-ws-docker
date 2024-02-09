package com.developer.app.ws.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

class MockServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream byteArrayInputStream;

    public MockServletInputStream(byte[] data) {
        this.byteArrayInputStream = new ByteArrayInputStream(data);
    }

    @Override
    public boolean isFinished() {
        return byteArrayInputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int read() throws IOException {
        return byteArrayInputStream.read();
    }
}