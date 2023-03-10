/*
 * Complex Number System
 *
 * Licensed under GNU LGPL V2.1
 * See LICENSE file for information
 *
 * Copyright (c) September 2021, Software Toolworks
 */
package complex;

public final class ComplexMath {

    private ComplexMath() {
    }

    public static Complex add(Complex a, Complex b) {
        return new Complex(a.getReal() + b.getReal(), a.getImaginary() + b.getImaginary());
    }

    public static Complex minus(Complex a, Complex b) {
        return new Complex(a.getReal() - b.getReal(), a.getImaginary() - b.getImaginary());
    }

    public static Complex times(Complex a, Complex b) {
        return new Complex(a.getReal() * b.getReal() - a.getImaginary() * b.getImaginary(),
                a.getReal() * b.getImaginary() + a.getImaginary() * b.getReal());
    }

    public static Complex times(Complex a, float alpha) {
        return new Complex(a.getReal() * alpha, a.getImaginary() * alpha);
    }

    public static Complex timesConjugate(Complex a, Complex b) {
        return new Complex(a.getReal() * b.getReal() + a.getImaginary() * b.getImaginary(),
                a.getImaginary() * b.getReal() - a.getReal() * b.getImaginary());
    }

    public static Complex conjugate(Complex a) {
        return new Complex(a.getReal(), -a.getImaginary());
    }

    public static Complex divide(Complex a, float b) {
        return new Complex(a.getReal() / b, a.getImaginary() / b);
    }

    public Complex divide(Complex a, Complex b) {
        float m = b.getReal() * b.getReal() + b.getImaginary() * b.getImaginary();
        return new Complex((a.getReal() * b.getReal() + a.getImaginary() * b.getImaginary()) / m,
                (a.getImaginary() * b.getReal() - a.getReal() * b.getImaginary()) / m);
    }

    public static Complex cneg(Complex a) {
        return new Complex(-a.getReal(), -a.getImaginary());
    }

    public static float csqr(Complex a) {
        return (a.getReal() * a.getReal()) + (a.getImaginary() * a.getImaginary());
    }

    public static float cabs(Complex a) {
        return (float) Math.sqrt(csqr(a));
    }

    public static Complex cexp(Complex a) {
        if (a.getReal() == 0.0f) {
            return new Complex((float) Math.cos(a.getImaginary()), (float) Math.sin(a.getImaginary()));
        } else {
            float expf = (float) Math.exp(a.getReal());

            return new Complex(expf * (float) Math.cos(a.getImaginary()),
                    expf * (float) Math.sin(a.getImaginary()));
        }
    }

    public static float carg(Complex a) {
        return (float) Math.atan2(a.getImaginary(), a.getReal() + 1E-12f);
    }

    public static Complex normalize(Complex a) {
        float mag = cabs(a);
        return new Complex(a.getReal() / mag, a.getImaginary() / mag);
    }
}
