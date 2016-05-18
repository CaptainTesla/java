public abstract class LavaggioAuto
{
    public abstract void prenotaParziale();
    public abstract void pagaParziale();
    public abstract void prenotaTotale();
    public abstract void lavaInterno();
    public abstract void pagaTotale();

}

public class VeicoloParziale extends Thread
{
    private int io;
    private LavaggioAuto la;
    
    public void run ()
    {
	// l'automobilista prenota il lavaggio esterno
	la.prenotaParziale();
	// l'automobilista entra nella zona A di lavaggio
	la.pagaParziale();
	// l'autista libera A ed esce dal lavaggio
    }

    public VeicoloParziale (int IO, LavaggioAuto LA)
    {
	this.io = IO;
	this.la = LA;
    }
}

public class VeicoloTotale extends Thread
{
    private int io;
    private LavaggioAuto la;
    
    public void run ()
    {
	// prenota il lavaggio totale
	la.prenotaTotale();
	// l'automobilista entra nella zona A
	la.lavaInterno();
	// l'automobilista entra nella zona B liberando la A
	la.pagaTotale();
	// l'automobilista lascia il lavaggio 
	
    }

    public VeicoloParziale (int IO, LavaggioAuto LA)
    {
	this.io = IO;
	this.la = LA;
    }
}

public class LavaggioAutoSemp extends LavaggioAuto
{
    private Semaphore attesaA = new Semaphore(false);
    private Semaphore attesaB = new Semaphore(false);
    private Semaphore mutex = new Semaphore(true);

    public abstract void prenotaParziale()
    {
	mutex.p();
	// richiedo l'accesso in A
	richiesteA++;
	
	// condizioni per entrare in A
	// posti liberi per il lavaggio parziale  non zero
	// che non ci siano richieste per il lavaggio totale in attesa
	if (postiLiberiA == 0 || richiesteTotale > 0) {
	    mutex.v();
	    attesaA.p();
	}
	// termino la richiesta all'entrata in A
	richiesteA--;
	// occupando un posto in A;
	postiLiberiA--;

	// possono sussistere: postiTotale != 0 && postiLiberiA != 0
	if (postiLiberiA != 0 && postiLiberiTotale != 0 && richiesteTotale > 0)
	    attesaB.v();
	// se non ho il problema di posti per il lavaggio totale verifico la presenza di quelli parziali
	if (postiLiberiA != 0 && richiesteA > 0)
	    attesaA.v();

	// sono a posto
	mutex.v();
	return;
    }
    
    public abstract void pagaParziale()
    {
	mutex.p();

	// libero un posto in A
	postiLiberiA++;
	// verifico se ci sono richieste per il lavaggio totale
	if (richiesteTotale > 0 && postiTotale !=0)
	    attesaB.v()
	// verifico se ci sono richeste per il parziale
	if (richiesteA > 0)
	    attesaA.v();

	// altrimenti sono a posto
	mutex.v();
	return;
    }
    
    public abstract void prenotaTotale()
    {
	mutex.p();
	// richedo l'accesso totale all'autolavaggio A & B
	richiesteTotale++;
	// condizioni per l'entrata in A && B:
	// che ci siano posti disponibili per il lavaggio totale
	if (postiLiberiTotale == 0) {
	    mutex.v();
	    attesaB.p();
	}
	// termino la richiesta
	richiesteTotale--;
	// ho occupato un posto per il totale
	postiLiberiTotale--;
	// ho occupato un posto in A
	postiLiberiA--;

	// a questo punto potrebbero essere soddisfatte: postiliberiA != 0 && postiliberitotale != 0:
	// condizioni necessarie per P&T
	if (postiLiberiA != 0 && postiLiberiTotale != 0 && richiesteTotale > 0)
	    attesaB.v();
	if (postiLiberiA != 0 && richiesteTotale == 0)
	    attesaA.v();

	// C.I. :
	mutex.v();
	return;
	
    }
    
    public abstract void lavaInterno()
    {
	mutex.p();
	
	// l'accesso alla zona B e' garantito: libero un posto in A
	postiLiberiA++;

	// ho garantito una condizione necessaria per i lavaggi totali e parziali
	// verifica ancora di tutte le condizioni di sincronizzazione
	if (postiLiberiTotale != 0 && richiesteTotale > 0)
	    attesaB.v();
	if (richiesteA > 0)
	    attesaA.v();

	// sono a posto altrimenti (condizioni iniziali)
	mutex.v();
	return;
    }
	
    public abstract void pagaTotale()
    {
	mutex.p();

	// uscendo dalla zona B libero un posto per il lavaggio totale
	// garantendo una condizione necessaria per i lavaggi P&T
	postiLiberiTotale++;
	// rivaluto le condizioni di sincronizzazione
	if (postiLiberiA != 0 && richiesteTotale > 0)
	    attesaB.v();
	// verifico le condizioni per i parziali
	if (postiLiberiA != 0 && richiesteA > 0)
	    attesaA.v();

	// altrimenti sono nelle condizioni iniziali
	mutex.v();
	return;
    }
    
} // LavaggioAutoSemP
