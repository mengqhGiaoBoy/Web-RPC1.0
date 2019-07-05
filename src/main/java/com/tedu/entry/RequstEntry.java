package com.tedu.entry;

import java.io.Serializable;

public class RequstEntry implements Serializable {
    private static final long serialVersionUID = 5868796643096647655L;
    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
