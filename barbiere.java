Semaphore negozio = new Semaphore(0,spazioNegozio);
Semaphore divano = new Semaphore(0,spazioDivano);
Semaphore sveglia = new Semaphore(false);
Semaphore clienteSeduto = new Semaphore(false);
Semaphore sediaLibera = new Semaphore(true);
Semaphore barbiere = new Semaphore(false);
Semaphore finito = new Semaphore(false);

/*
  CONDIZIONI DI SINCRONIZZAZIONE
  PER ANDARE DAL BARBIERE DAL DIVANO O APPENA ENTRO: POSTI DAVANTI == 0 && BARBIERE == LIBERO
  PER SEDERSI SUL DIVANO STANDO IN PIEDI: POSTIDIVANO != 0
  PER STARE IN ATTESA IN PIEDI NEL NEGOZIO: POSTINEGOZIO != 0 && TEMPODIATTESA < TM
  PER CHIUDERE IL NEGOZIO : ORARIO == ORARIOCHIUSURA
  PER METTERE A RIPOSO IL BARBIERE : CLIENTIINATTESA == 0 && SEDIALIBERA 
 */


public class Barbiere extends Thread
{
    private int tempoLavoro = 0;

    public void run() {
	while (tempoLavoro < TEMPOCHIUSURA) {
	    if (postiNegozio == 10) { sveglia.p(); }
	    barbiere.v();
	    finito.v();
	    sediaLibera.p();
	}
	chiuso = true;
	while (postiNegozio != 10) {
	    barbiere.v();
	    finito.v();
	    sediaLibera.p();
	}
	sveglia.p();
	return;
    }
	
}

public boolean chiuso = false;
public int postiNegozio = 10;
public int postiDivano = 5;
public int TEMPOCHIUSURA = 120;
public int TEMPOATTESAMAX = 30;

public class Cliente extends Thread
{
    private int tempoAttesa = 0;

    private 

    public Cliente(SharedRes RES) { this.res = RES; }

    public void run()
    {

	if (postiNegozio == 0 or chiuso) {
	    return;
	}
	negozio.p();
	postiNegozio--;
	if (postiNegozio == 9) {
	    sveglia.v();
	    postiNegozio++;
	    negozio.v();
	    barbiere.p();
	    finito.p();
	    sediaLibera.v();
	    return;
	}
	if (postiDivano != 0) {
	    postiDivano--;
	    divano.p();
	    barbiere.p();
	}
	clienteSeduto.p();
	if (tempoAttesa == TEMPOATTESAMAX) {
	    negozio.v();
	    postiNegozio++;
	    return;
	}
	// mi accomodo, aspetto il barbiere e poi libero la sedia e il negozio quando ho pagato
	clienteSeduto.v();
	negozio.v();
	divano.v();
	postiDivano++; postiNegozio++;
	finito.p();
	sediaLibera.v();
	return;
    }
    
}

