package com.shrill.falseSharing;

import sun.misc.Contended;

@Contended
public class VolatileLong {
    public volatile long value = 0L;
}
