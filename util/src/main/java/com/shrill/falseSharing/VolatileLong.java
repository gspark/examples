package com.shrill.falseSharing;

import jdk.internal.vm.annotation.Contended;
// import sun.misc.Contended;

@Contended
public class VolatileLong {
    public volatile long value = 0L;
}
