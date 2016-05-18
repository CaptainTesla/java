/*
  per verificare la correttezza del codice:
  ad ogni incremento di variabile deve corrispondere nel flusso di esecuzione un corrispondente 
  decremento 
  ps il codice purtroppo non e' una corrispondenza 1 a 1 con la rete di petri
 */


public abstract class TorreDiControllo 
{
    
    public abstract void richAccessoPista(int IO);
    
    public abstract void richAutorizDecollo(int IO);
    
    public abstract void inVolo(int IO);
    
    public abstract void richAutorizAtterraggio(int IO);
    
    public abstract void freniAttivati(int IO);
    
    public abstract void inParcheggio(int IO);

    public void stampaSituazioneAreoporto()
    {
	// ... //
	System.out.Println("Posti Liberi in A: " + postiLiberiA);
	System.out.Println("Posti Liberi in B: " + postiLiberiB);
    }

    // condizioni iniziali
    protected int richiestaAccessoA = 0;
    protected int richiestaAccessoB = 0;
    protected int richiesteAtterraggio = 0;
    protected int postiLiberiA = 2;
    protected int postiLiberiB = 2;
    protected boolean atterraggioInCorso = false;

}

public class TorreDiControlloSemp extends TorreDiControllo
{
    private Semaphore mutex = new Semaphore();
    private Semaphore attesaA = new Semaphore();
    private Semaphore attesaB = new Semaphore();
    private Semaphore attesaAtterraggio = new Semaphore();

    
    public void richAccessoPista(int IO)
    {
	mutex.p();
	
	// richiedo l'accesso ad A
	richiestaAccessoA++;
	// se c'e' un atterraggio in corso o se ci sono richieste di atterraggio o 0 posti liberi attendo
	if ( atterraggioInCorso || richiesteAtterraggio > 0 || postiLiberiA == 0 ) {
	    mutex.v();
	    attesaA.p();
	}

	// termino la richiesta ed entro in A
	richiestaAccessoA--;
	postiLiberiA--;

	mutex.v();
	return;
    }
	
    public void richAutorizDecollo(int IO)
    {
	mutex.p();

	// richiedo l'accesso in B
	richiestaAccessoB++;
	// le condizioni N&S per B sono:
	// che ci siano posti liberi (non devo verificare l'atterraggio perche' sono gia' in zona A e non garantisco una condizione necessaria per esso)
	if ( postiLiberiB == 0 ) {
	    mutex.v();
	    attesaB.p();
	}

	// mi termino la richiesta ed entro in B occupando un posto e liberandone uno in A
	richiestaAccessoB--;
	postiLiberiA++;
	postiLiberiB--;
	// ho appena garantito una condizione necessaria per l'accesso ad A: verifico se ci sono aerei in attesa 
	if ( attesaAtterraggio == 0 && richiestaAccessoA > 0 )
	    attesaA.v();
	
	mutex.v();
	return;
    }
    
    public void inVolo(int IO)
    {
	mutex.p();

	// libero un posto in B
	postiLiberiB++;
	// garantendo una condizione necessaria per decollo e atterraggio
	// verifico se ci sono aerei che desiderano decollare
	if ( richiestaAccessoB > 0 )
	    attesaB.v();
	// a questo punto se non ci sono aerei in B ho una condizione necessaria per l'atterraggio: verifico che A libera prima di garantire l'atterraggio
	if ( richestaAtterraggio > 0 && postiLiberiA == 2 )
	    attesaAtterraggio.v();
	// se non ho ne' decolli ne' atterraggi pendenti allora posso far entrare Aerei in A 
	if ( richiestaAccessoA > 0 )
	    attesaA.v();
	// altrimenti sono nelle condizioni iniziali
	
	mutex.v();
	return;
    }
    
    public void richAutorizAtterraggio(int IO)
    {
	mutex.p();

	// inoltro la richiesta di atterraggio
	richiesteAtterraggio++;
	// le condizioni N&S per l'atterraggio sono
	// la pista libera: A && B liberi
	if ( postiLiberiA < 2 && postiLiberiB < 2) {
	    mutex.v();
	    attesaAtterraggio.p();
	};
	// se atterro termino la richiesta e segnalo l'atterraggio e occupo due posti in A e B
	richiesteAtterraggio--;
	atterraggioInCorso = true;
	postiLiberiA -= 2;
	postiLiberiB -= 2;

	mutex.v();
	return;

    }
    
    public void freniAttivati(int IO)
    {
	mutex.p();

	// libero la zona A
	postiLiberiA += 2;
	// garantendo una condizione necessaria per l'accesso in pista
	// tuttavia atterraggioInCorso mi impedisce di garantire l'accesso ad A
	
	mutex.v();
	return;
    
    public void inParcheggio(int IO)
    {
	// a questo punto libero anche B
	postiLiberiB += 2;
	// terminando l'atterraggio
	atterraggioInCorso = false;
	// verifico prima di tutto se ci sono atterraggi pendenti dal momento che a questo punto sono garantite tutte le cond. N&S
	if ( attesaAtterraggio > 0 )
	    attesaAtterraggio.v();
	// ora non avendo piu' atterraggi ed essendo A e B libere garantisco l'accesso in pista
	if ( richiestaAccessoA > 0 )
	    attesaA.v();
	// altrimenti sono nelle condizioni iniziali
	mutex.v();
	return;
    }
}
