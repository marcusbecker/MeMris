/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.memris;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JPanel;

/**
 *
 * @author mbecker
 */
public class Game extends javax.swing.JFrame {

    /**
     * Creates new form Game
     */
    private final int[][] gradeEspaco = new int[14][16];
    private int[][] p;// Peca em jogo
    private Color c;
    private int pecaSelIdx;

    private Thread gameUpdate;
    private Peca fabricaPeca = new Peca();
    private boolean gameOn = false;

    // Posicao peca x,y
    private int ppx;
    private int ppy;

    //Level
    private int lv = 1;
    private int linhasFeistas = 0;
    private int pontos = 0;

    private final int LINHA_VAZIA = -1;
    private final int LINHA_COMPLETA = -2;

    private JPanel criarGrade() {

        for (int i = 0; i < gradeEspaco.length; i++) {
            for (int j = 0; j < gradeEspaco[i].length; j++) {
                gradeEspaco[i][j] = LINHA_VAZIA;
            }
        }

        return new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                // Tamanho do espaco para desenhar a grade
                final int w = grade.getWidth();
                final int h = grade.getHeight();

                // Largura do espado da peca na grade
                final int pw = w / gradeEspaco.length;
                // Altura do espado da peca na grade
                final int ph = h / gradeEspaco[0].length;
                // desenhar fundo
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, w, h);

                // desenhar grade
                for (int i = 0; i < gradeEspaco.length; i++) {
                    int[] linha = gradeEspaco[i];
                    for (int j = 0; j < linha.length; j++) {

                        if (gradeEspaco[i][j] > LINHA_VAZIA) {
                            g.setColor(Peca.Cores[gradeEspaco[i][j]]);
                            g.fillRect(i * pw, j * ph, pw, ph);
                        }

                        if (gradeEspaco[i][j] == LINHA_COMPLETA) {
                            g.setColor(Color.RED);
                            g.fillRect(i * pw, j * ph, pw, ph);
                        }
                    }
                }

                // desenhar pecas
                if (p == null) {
                    return;
                }

                g.setColor(c);
                for (int i = 0; i < p.length; i++) {
                    int[] l = p[i];
                    for (int j = 0; j < l.length; j++) {
                        if (p[i][j] != 0) {
                            g.fillRect((i + ppx) * pw + 2, (j + ppy) * ph + 2, pw - 2, ph - 2);
                        }
                    }
                }
            }
        };
    }

    /**
     * Movimento x
     *
     * @param peca
     * @param mx
     * @return
     */
    public boolean validaMovimento(int[][] peca, int mx) {
        for (int i = 0; i < peca.length; i++) {
            for (int j = 0; j < peca[i].length; j++) {
                if (peca[i][j] != 0) {
                    int prxPX = i + mx; // Proxima posicao peca x

                    if (prxPX < 0 || prxPX > gradeEspaco.length - 1) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Detectar colisao
     *
     * @param peca
     * @param mx
     * @param my
     * @return
     */
    public boolean colidiu(int[][] peca, int mx, int my) {
        for (int i = 0; i < peca.length; i++) {
            for (int j = 0; j < peca[i].length; j++) {
                if (peca[i][j] != 0) {
                    int prxPX = i + mx;
                    int prxPY = j + my;

                    if (prxPY < 0) {
                        return false;
                    }

                    if (prxPY > gradeEspaco[0].length - 1) {
                        return true;
                    }

                    if (prxPX < 0 || prxPX == gradeEspaco.length) {
                        continue;
                    }

                    if (gradeEspaco[prxPX][prxPY] > LINHA_VAZIA) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Game() {
        initComponents();
        gameUpdate = new Thread() {
            private long ups;
            private long fps;

            @Override
            public void run() {
                ups = System.currentTimeMillis();
                fps = ups;
                while (true) {
                    long atual = System.currentTimeMillis();
                    // FPS
                    if (atual - fps > 20) {
                        grade.repaint();
                        fps = System.currentTimeMillis();
                    }

                    // UPS
                    int t = 500 - (lv * 100 / 2);
                    if (atual - ups > t) {
                        atualizarJogo();
                        ups = System.currentTimeMillis();
                    }
                }
            }
        };
    }

    public void iniciarJogo() {
        adicionaNovaPeca();
        gameUpdate.start();
        gameOn = true;
    }

    private void atualizarJogo() {
        if (!gameOn) {
            return;
        }

        if (colidiu(p, ppx, ppy + 1)) {
            //Se a peca apareceu na grade
            if (ppy + 1 > 0) {
                adicionaPecaNaGrade();

                marcarColuna();

                descerColunas();

                adicionaNovaPeca();

            } else {
                // game over
                gameOn = false;
                log("game over");
            }
        } else {
            //Nao colidiu, continua descendo
            ppy++;
        }
    }

    private void marcarColuna() {
        int multPontos = 0;
        for (int j = gradeEspaco[0].length - 1; j > 0; j--) {
            boolean linhaCompleta = true;
            for (int i = gradeEspaco.length - 1; i > 0; i--) {
                if (gradeEspaco[i][j] <= LINHA_VAZIA) {
                    linhaCompleta = false;
                    break;
                }
            }

            if (linhaCompleta) {
                multPontos++;
                for (int[] coluna : gradeEspaco) {
                    coluna[j] = LINHA_COMPLETA;
                }
            }
        }

        pontos += lv * (multPontos * multPontos);
        linhasFeistas += multPontos;
        log("pontos ", pontos);

        if (lv == 9 && linhasFeistas == 10) {
            gameOn = false;
            log("Win!");
            
        } else if (linhasFeistas == 10) {
            lv++;
            linhasFeistas = 0;
            log("Level", lv);
        }
    }

    private void descerColunas() {
        for (int col = 0; col < gradeEspaco.length; col++) {
            for (int ln = gradeEspaco[col].length - 1; ln >= 0; ln--) {
                if (gradeEspaco[col][ln] == LINHA_COMPLETA) {
                    int mvPara = ln;
                    int prx = ln - 1;
                    for (int j = ln; j > 0; j--) {
                        if (gradeEspaco[col][prx] == LINHA_COMPLETA) {
                            prx--;
                            continue;
                        }

                        gradeEspaco[col][mvPara] = gradeEspaco[col][prx];
                        mvPara--;
                        prx--;
                    }
                    //gradeEspaco[col][0] = LINHA_VAZIA;
                }
            }
        }
    }

    private void imprimirGrade() {
        for (int i = 0; i < gradeEspaco.length; i++) {
            int[] linha = gradeEspaco[i];
            for (int j = 0; j < linha.length; j++) {

                log(i + "x" + j, gradeEspaco[i][j]);
            }
        }
    }

    private int[][] virarPeca(boolean esquerda) {
        int x, y, vx, vy;
        int[][] temp = new int[p.length][p[0].length];
        int size = p.length;
        for (x = 0, vx = size - 1; x < size; x++, vx--) {
            for (y = size - 1, vy = 0; y >= 0; y--, vy++) {
                if (esquerda) {
                    temp[vy][x] = p[x][y];
                } else {
                    temp[vx][vy] = p[y][vx];
                }
            }
        }
        return temp;
    }

    private void adicionaPecaNaGrade() {

        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < p[i].length; j++) {

                if (p[i][j] != 0) {
                    gradeEspaco[i + ppx][j + ppy] = pecaSelIdx;
                }
            }
        }
    }

    private void adicionaNovaPeca() {
        ppy = -2;
        ppx = gradeEspaco.length / 2 - 1;
        p = fabricaPeca.gerarPeca();
        c = Peca.Cores[fabricaPeca.getPecaId()];
        pecaSelIdx = fabricaPeca.getPecaId();
    }

    private void movePeca(int evt) {
        if (!gameOn) {
            return;
        }
        /*
         * 38 cima | 40 baixo | 37 esq | 39 dir
         */
        // Proximo movimento x,y
        int pmx = ppx;
        int pmy = ppy;
        int[][] prev = p;
        // System.out.println(ppx + "x" + ppy);
        switch (evt) {
            case 38:
                prev = virarPeca(true);
                break;
            case 40:
                pmy++;
                break;
            case 37:
                pmx--;
                break;
            case 39:
                pmx++;
                break;
        }

        if (!colidiu(prev, pmx, pmy) && validaMovimento(prev, pmx)) {
            ppx = pmx;
            ppy = pmy;
            p = prev;
        }

    }

    // private void
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		grade = criarGrade();
		jPanel1 = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				formKeyPressed(evt);
			}

			public void keyReleased(java.awt.event.KeyEvent evt) {
				formKeyReleased(evt);
			}
		});

		javax.swing.GroupLayout gradeLayout = new javax.swing.GroupLayout(grade);
		grade.setLayout(gradeLayout);
		gradeLayout.setHorizontalGroup(gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 328, Short.MAX_VALUE));
		gradeLayout.setVerticalGroup(gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 110, Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 317, Short.MAX_VALUE));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addComponent(grade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(grade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup().addContainerGap()
								.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

    private void formKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_formKeyReleased
        // movePeca(evt.getKeyCode());
    }// GEN-LAST:event_formKeyReleased

    private void formKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_formKeyPressed
        movePeca(evt.getKeyCode());
    }// GEN-LAST:event_formKeyPressed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel grade;
	private javax.swing.JPanel jPanel1;

	// End of variables declaration//GEN-END:variables
    private void log(String str, int... param) {
        System.out.print(str);
        for (int p : param) {
            System.out.print(" " + p);
        }

        System.out.println();
    }

}
