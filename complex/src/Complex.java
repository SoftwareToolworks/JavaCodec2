/*
 * Complex Number System
 *
 * Licensed under GNU LGPL V2.1
 * See LICENSE file for information
 *
 * Copyright (c) September 2021, Software Toolworks
 */
package complex;

public final class Complex {

    private final float real;
    private final float imag;

    public Complex() {
        real = 0.0f;
        imag = 0.0f;
    }

    public Complex(float re, float im) {
        real = re;
        imag = im;
    }

    public float getReal() {
        return real;
    }

    public float getImaginary() {
        return imag;
    }
}

