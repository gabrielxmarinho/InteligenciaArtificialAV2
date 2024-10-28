package agente;

import algoritmo.ProgramaLadrao;

public class Ladrao extends ProgramaLadrao {
	//
	private double[] probs = new double[5]; 
	
	public int acao() {
		this.calcularProbs();
		return this.roleta();
	}
	
	public void calcularProbs(){
		double soma =0;
		double[]f=new double[5];
		for(int i=0;i<this.sensor.getVisaoIdentificacao().length;i++) {
			double[] distancias =this.dist(i);
			
				if(distancias[0]>0) {
					f[3]+=this.f(this.sensor.getVisaoIdentificacao()[i], Math.abs(distancias[0]),this.bloqueado(3));
				}else if(distancias[0]<0) {
					f[4]+=this.f(this.sensor.getVisaoIdentificacao()[i], Math.abs(distancias[0]),this.bloqueado(4));
				}
				if(distancias[1]>0) {
					f[1]+=this.f(this.sensor.getVisaoIdentificacao()[i], Math.abs(distancias[1]),this.bloqueado(1));
				}else if(distancias[1]<0) {
					f[2]+=this.f(this.sensor.getVisaoIdentificacao()[i], Math.abs(distancias[1]),this.bloqueado(2));
				}
			}
		//System.out.printf("%f %f %f %f %f\n",f[0],f[1],f[2],f[3],f[4]);
		//Calculando os cheiros(g1 e g2)
		for(int i=0;i<this.sensor.getAmbienteOlfatoPoupador().length;i++) {
			if(this.sensor.getAmbienteOlfatoPoupador()[i]>0) {
				if(i==0 || i==3 || i==5) {
					f[4]+=this.g1(this.sensor.getAmbienteOlfatoPoupador()[i]);
				}else if(i==2 || i==4 || i==7) {
					f[3]+=this.g1(this.sensor.getAmbienteOlfatoPoupador()[i]);
				}
				if(i<3) {
					f[1]+=this.g1(this.sensor.getAmbienteOlfatoPoupador()[i]);
				}else if(i>4) {
					f[2]+=this.g1(this.sensor.getAmbienteOlfatoPoupador()[i]);
				}
			}
		} 
		for(int i=0;i<this.sensor.getAmbienteOlfatoLadrao().length;i++) {
			if(this.sensor.getAmbienteOlfatoLadrao()[i]>0) {
				if(i==0 || i==3 || i==5) {
					f[4]+=this.g2(this.sensor.getAmbienteOlfatoLadrao()[i]);
				}else if(i==2 || i==4 || i==7) {
					f[3]+=this.g2(this.sensor.getAmbienteOlfatoLadrao()[i]);
				}
				if(i<3) {
					f[1]+=this.g2(this.sensor.getAmbienteOlfatoLadrao()[i]);
				}else if(i>4) {
					f[2]+=this.g2(this.sensor.getAmbienteOlfatoLadrao()[i]);
				}
			}
		}
		
		for(int i=0;i<5;i++) {
			soma+=f[i];
		}
		for (int i=0;i<5;i++) {
			this.probs[i]=f[i]/soma;
		}
	}
	
	
	public static double f(double k,double x,double b) {
		return x*Math.abs(k-1)*Math.abs(199-k)*Math.pow(Math.E, -b*(Math.pow(x-1, 2)))/(200*b);
	}
	
	public static double g1(double t) {
		return 1000*Math.pow(2, -t);
	}
	public static double g2(double t) {
		return 5*Math.pow(2, -t);
	}
	public double[] dist(int indice) {
		int dx, dy;
		if(indice<5) {
			dx = indice-2;
		}else if(indice>18) {
			dx = indice-21;
		}else {
			if(indice<10) {
				dx = indice-7;
			}else if(indice>13) {
				dx = indice -16;
			}else {
				if(indice>11) {
					dx = indice-11;
				}else {
					dx = indice-12;
				}
			}
		}
		if(indice!=14 && indice!=19) {
			dy = 2-indice/5;
		}else {
			if(indice==14) {
				dy = 1;
			}else {
				dy = 2;
			}
		}
		double distancias[] = {dx,dy};
		return distancias;
	}
	
	public double bloqueado(int posicao) {
		int movimento=0;
		if(posicao==1) {
			movimento = 7;
		}else if(posicao==2) {
			movimento = 16;
		}else if(posicao ==3) {
			movimento = 12;
		}else if(posicao ==4) {
			movimento = 11;
		}else {
			return 1;
		}
		if (this.sensor.getVisaoIdentificacao()[movimento]!=0 && this.sensor.getVisaoIdentificacao()[movimento]<100) {
			return 10000;
		}else {
			return 1;
		}
	}
	public int roleta() {
		double x = Math.random();
		int cont=0;
		double soma=this.probs[0];
		while(soma<x && cont<4) {
			cont++;
			soma+=this.probs[cont];
		}
		return cont;
		
	}
}