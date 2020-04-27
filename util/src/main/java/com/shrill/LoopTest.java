package com.shrill;

public class LoopTest {


    public static void main(String[] args) {
        LoopTest lt = new LoopTest();
        int ret = lt.loop1(3, 5, new int[]{1, 2, 3});
        System.out.println("ret = " + ret);
    }

    public int loop1(int x, int y, int[] ary) {
        int sum = 0;
        for (int i = 0; i < ary.length; i++) {
            sum += x * y + ary[i];
        }
        return sum;
    }

    public int loop1Opt(int x, int y, int[] ary) {
        int sum = 0;
        int t0 = x * y;
        int t1 = ary.length;
        for (int i = 0; i < t1; i++) {
            sum += t0 + ary[i];
        }
        return sum;
    }

    public int iaload(int[] aryRef, int index) {
        if (aryRef == null) {
            // null 检测
            throw new NullPointerException();
        }
        if (index < 0 || index >= aryRef.length) {
            // 下标范围检测
            throw new ArrayIndexOutOfBoundsException();
        }
        return aryRef[index];
    }

    public int loop1(int[] a) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            if (a == null) {
                // null check
                throw new NullPointerException();
            }
            if (i < 0 || i >= a.length) {
                // range check
                throw new ArrayIndexOutOfBoundsException();
            }
            sum += a[i];
        }
        return sum;
    }

    public int loop2(int[] a) {
        int sum = 0;
        if (a == null) {
            // never returns
            // deoptimize();
        }

        for (int i = 0; i < a.length; i++) {
            if (a == null) {
                // now evluate to false
                throw new NullPointerException();
            }
            if (i < 0 || i >= a.length) {
                // range check
                throw new ArrayIndexOutOfBoundsException();
            }
            sum += a[i];
        }
        return sum;
    }

    public int loop3(int[] ary) {
        int sum = 0;
        for (int i = 0; i < 64; i++) {
            sum += (i % 2 == 0) ? ary[i] : -ary[i];
        }
        return sum;
    }

    public int loop3Opt(int[] ary) {
        int sum = 0;
        for (int i = 0; i < 64; i += 2) {
            // 注意这里的步数是 2
            sum += (i % 2 == 0) ? ary[i] : -ary[i];
            sum += ((i + 1) % 2 == 0) ? ary[i + 1] : -ary[i + 1];
        }
        return sum;
    }

    public int loop3Opt1(int[] ary) {
        int sum = 0;
        for (int i = 0; i < 64; i += 2) {
            sum += ary[i];
            sum += -ary[i + 1];
        }
        return sum;
    }

    public int loop4(int[] ary) {
        int sum = 0;
        for (int i = 0; i < ary.length; i++) {
            if (ary.length > 4) {
                sum += ary[i];
            }
        }
        return sum;
    }

    public int loop4Opt(int[] ary) {
        int sum = 0;
        if (ary.length > 4) {
            for (int i = 0; i < ary.length; i++) {
                sum += ary[i];
            }
        } else {
            for (int i = 0; i < ary.length; i++) {
            }
        }
        return sum;
    }

    public int loop4Opt1(int[] ary) {
        int sum = 0;
        if (ary.length > 4) {
            for (int i = 0; i < ary.length; i++) {
                sum += ary[i];
            }
        }
        return sum;
    }

    public int loop5(int[] ary) {
        int j = 0;
        int sum = 0;
        for (int i = 0; i < ary.length; i++) {
            sum += ary[j];
            j = i;
        }
        return sum;
    }

    public int loop5Opt(int[] ary) {
        int sum = 0;
        if (0 < ary.length) {
            sum += ary[0];
            for (int i = 1; i < ary.length; i++) {
                sum += ary[i - 1];
            }
        }
        return sum;
    }
}

