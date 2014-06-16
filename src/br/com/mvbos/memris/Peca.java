/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.memris;

import java.awt.Color;

/**
 *
 * @author mbecker
 */
public class Peca {

    public static Color[] Cores = {
        Color.GREEN, Color.ORANGE
    };
    public static final int[][][] PECA = {
        {{0, 1, 0},
        {1, 1, 0},
        {0, 0, 0}},
        {{1, 0, 1},
        {1, 0, 1},
        {0, 1, 0}}
    };
}
