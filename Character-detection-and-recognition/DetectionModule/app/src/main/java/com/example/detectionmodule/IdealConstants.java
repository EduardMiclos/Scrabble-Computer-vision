package com.example.detectionmodule;

/* These are empirical constants that won't dynamically
change during program execution. */

public interface IdealConstants {

    /* --- RECTANGLE BOUNDS --- */
    int rectangleMinHeight = 20;
    int rectangleMaxHeight = 36;

    int rectangleMinWidth = 4;
    int rectangleMaxWidth = 33;
    /* ------------------------ */

    /* --- MAXIMUM HORIZONTAL AND VERTICAL DEVIATION ERROR --- */
    int maxCenterErrorX = 300;
    int maxCenterErrorY = 50;
    /* ------------------------------------------------------- */

    /* --- ADAPTIVE THRESHOLD --- */
    int blockSize = 11; /* Odd number. */
    double constantC = 15;
    /* -------------------------- */
}
