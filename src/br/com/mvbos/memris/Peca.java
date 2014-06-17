/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.memris;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author mbecker
 */
public class Peca {

    public static Color[] Cores = {
        Color.GREEN, Color.ORANGE, Color.YELLOW, Color.CYAN, Color.BLUE, Color.MAGENTA
    };

    private static final int[][][] PECA = {
        {
            {0, 1, 0},
            {1, 1, 0},
            {0, 0, 0}},
        {
            {1, 0, 1},
            {0, 1, 0},
            {0, 0, 0}},
        {
            {0, 1, 0},
            {0, 1, 0},
            {0, 1, 0}},
        {
            {0, 1, 0},
            {0, 1, 0},
            {1, 0, 0}},
        {
            {0, 1, 0},
            {0, 1, 0},
            {0, 0, 1}}
    };

    private int pecaId;
    private final Random rand = new Random();

    public int[][] gerarPeca() {
        pecaId = rand.nextInt(Peca.PECA.length);

        if (rand.nextInt(100) == 10) {
            int[][] bug = new int[PECA.length][PECA.length];
            int cont = 0;
            for (int ln = 0; ln < bug.length; ln++) {
                for (int col = 0; col < bug.length; col++) {
                    int t = rand.nextInt(2);
                    bug[ln][col] = t;
                    cont += t;
                    if (cont >= 5) {
                        break;
                    }
                }

            }

            return bug;
        }

        return PECA[pecaId];
    }

    public int getPecaId() {
        return pecaId;
    }
}
