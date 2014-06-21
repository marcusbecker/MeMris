var gradeEspaco;
var p;// atalho para peca em jogo
var pecaSelIdx = 0;
var estado;
// Posicao peca x,y
var ppx = 0;
var ppy = 0;

//Level
var lv = 1;
var linhasFeistas = 0;
var pontos = 0;

var grade;
var g;

var imgFundo;

function paintComponent(){
	// Tamanho do espaco para desenhar a grade
	var w = grade.width;
	var h = grade.height;

	// Largura do espado da peca na grade
	var pw = w / gradeEspaco.length;
	// Altura do espado da peca na grade
	var ph = h / gradeEspaco[0].length;	
	
	//g.fillStyle = "#000";
	//g.fillRect(0, 0, w, h);
	g.drawImage(imgFundo, 0, 0);
	// desenhar grade
	for (var i = 0; i < gradeEspaco.length; i++) {
		linha = gradeEspaco[i];
		for (var j = 0; j < linha.length; j++) {

			if (gradeEspaco[i][j] > LINHA_VAZIA) {
				g.fillStyle = COR[gradeEspaco[i][j]];
				g.fillRect(i * pw, j * ph, pw, ph);
			}

			if (gradeEspaco[i][j] == LINHA_COMPLETA) {
				g.fillStyle = "#FF0000";
				g.fillRect(i * pw, j * ph, pw, ph);
			}
		}
	}

	if (p != undefined) {
		g.fillStyle = COR[pecaSelIdx];
		for (var i = 0; i < p.length; i++) {
			l = p[i];
			for (var j = 0; j < l.length; j++) {
				if (p[i][j] != 0) {
					g.fillRect((i + ppx) * pw + 2, (j + ppy) * ph + 2, pw - 2, ph - 2);
				}
			}
		}
	}

	g.fillStyle = "#FFF";
	g.font = "20px Arial";
	g.fillText("Level " + lv, 5, 20);
	g.fillText("Pontos " + (pontos * 100), w - 140, 20);
	
	if (estado == PAUSADO) {
		g.fillText("-_-_- Pausa -_-_-", w / 2 - 85, h / 2);
	} else if (estado == GANHO) {
		g.fillText("-_-_- Ganhou! -_-_-", w / 2 - 85, h / 2);
	} else if (estado == PERDIDO) {
		g.fillText("-_-_- Perdeu! -_-_-", w / 2 - 85, h / 2);
	}
	
	g.fillStyle = "#94c400";
	g.fillText("Tetris Especial 99Vidas", 80, 20);	
	
}//paint

function movePeca(evt) {

	if (estado != NAOPAUSADO && estado != PAUSADO) {
		return;
	}

	/*
	 * 38 cima | 40 baixo | 37 esq | 39 dir
	 */
	// Proximo movimento x,y
	var pmx = ppx;
	var pmy = ppy;
	var prev = p;

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
		default:
			estado = estado == PAUSADO ? NAOPAUSADO : PAUSADO;
	}

	if (estado == PAUSADO) {
		return;
	}

	if (!colidiu(prev, pmx, pmy) && validaMovimento(prev, pmx)) {
		ppx = pmx;
		ppy = pmy;
		p = prev;
	}

}//movePeca	

function atualizarJogo() {
	if (estado != NAOPAUSADO) {
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
			estado = PERDIDO;
			//console.log("game over");
		}
	} else {
		//Nao colidiu, continua descendo
		ppy++;
	}
}//atualizarJogo	

function virarPeca(esquerda) {
	var x, y, vx, vy;
	var temp = [[],[],[]];
	
	var size = p.length;
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
}//virar peça	

function colidiu(peca, mx, my) {
	for (var i = 0; i < peca.length; i++) {
		for (var j = 0; j < peca[i].length; j++) {
			if (peca[i][j] != 0) {
				prxPX = i + mx;
				prxPY = j + my;

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
}//colisao


function marcarColuna() {
	var multPontos = 0;
	for (var j = gradeEspaco[0].length - 1; j >= 0; j--) {
		linhaCompleta = true;
		for (var i = gradeEspaco.length - 1; i >= 0; i--) {
			if (gradeEspaco[i][j] <= LINHA_VAZIA) {
				linhaCompleta = false;
				break;
			}
		}

		if (linhaCompleta) {
			multPontos++;
			for (var i = gradeEspaco.length - 1; i >= 0; i--) {
				gradeEspaco[i][j] = LINHA_COMPLETA;
			}
		}
	}

	pontos += lv * (multPontos * multPontos);
	linhasFeistas += multPontos;
	//console.log("pontos ", pontos);

	if (lv == 9 && linhasFeistas == 7) {
		estado = GANHO;
		//console.log("Win!");

	} else if (linhasFeistas == 7) {
		lv++;
		linhasFeistas = 0;
		console.log("Level", lv);
	}
}//marcarColuna

function descerColunas() {
	for (var col = 0; col < gradeEspaco.length; col++) {
		for (var ln = gradeEspaco[col].length - 1; ln >= 0; ln--) {
			if (gradeEspaco[col][ln] == LINHA_COMPLETA) {
				mvPara = ln;
				prx = ln - 1;
				for (var j = ln; j > 0; j--) {
					if (gradeEspaco[col][prx] == LINHA_COMPLETA) {
						prx--;
						continue;
					}

					gradeEspaco[col][mvPara] = gradeEspaco[col][prx];
					mvPara--;
					prx--;
				}
				gradeEspaco[col][0] = LINHA_VAZIA;
			}
		}
	}
}//descerColunas	

function validaMovimento(peca, mx) {
	for (var i = 0; i < peca.length; i++) {
		for (var j = 0; j < peca[i].length; j++) {
			if (peca[i][j] != 0) {
				prxPX = i + mx; // Proxima posicao peca x

				if (prxPX < 0 || prxPX > gradeEspaco.length - 1) {
					return false;
				}
			}
		}
	}

	return true;
}//valida movimento	

function adicionaNovaPeca() {
	ppy = -2;
	ppx = parseInt(gradeEspaco.length / 2 - 1);
	random = Math.floor((Math.random() * PECA.length));
	if(random == pecaSelIdx){
		//Peca repetida, tentar novamente
		random = Math.floor((Math.random() * PECA.length));
	}
	p = PECA[random];//fabricaPeca.gerarPeca();
	pecaSelIdx = random;
}//adicionaNovaPeca

function adicionaPecaNaGrade() {

	for (var i = 0; i < p.length; i++) {
		for (var j = 0; j < p[i].length; j++) {

			if (p[i][j] != 0) {
				gradeEspaco[i + ppx][j + ppy] = pecaSelIdx;
			}
		}
	}
}//adicionaPecaNaGrade



function iniciarJogo(canvas) {
	grade = document.getElementById(canvas);
	g = grade.getContext("2d");
	
	imgFundo = document.getElementById("img_fundo");

	gradeEspaco = [];
	var gradeLinhas = 12;
	var gradeColunas = 14;
	for(var i = 0; i < gradeLinhas; i++) {
		gradeEspaco.push(new Array(gradeColunas));
	}

	for(var i = 0; i < gradeLinhas; i++){
		for(var j = 0; j < gradeEspaco[i].length; j++){
			gradeEspaco[i][j] = -1;
		}
	}
	
	var ups, fps, atual, gameUpdate;		

	fps = 20;
	ups = new Date().getTime();
	atual = ups;
	gameUpdate = setInterval(function(){
			// UPS
			t = 500 - (lv * 100 / 2);
			if (atual - ups > t) {
				atualizarJogo();
				ups = new Date().getTime();
			}
			
			paintComponent();
			atual = new Date().getTime();
	}, fps);
	
	adicionaNovaPeca();
	estado = NAOPAUSADO;
}

document.onkeydown = function() {
	movePeca(window.event.keyCode);
};	