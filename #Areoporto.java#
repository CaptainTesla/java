hpublic abstract class TorreDiControllo 
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

    protected int codaDecolloA = 0;
    protected int codaDecolloB = 0;
    protected int codaAtterraggio = 0;
    
    protected int atterraggiInCorso = 0;
    protected int postiLiberiA = 2;
    protected int postiLiberiB = 2;
	
}

public class AereoCheDecolla extends Thread
{
    
    private int io; // numero dell'aereo
    private TorreDiControllo tc; // classe che incapsula lo stato e i modi per la sincronizzazione

    
    public AereoCheDecolla (int IO, TorreDiControllo torre)
    {
	this.io = ioNumber;
	this.tc = torre;
    }

    public void run()
    {
	// il pilota e' pronto a entare in pista
	tc.richAccessoPista(io);
	// il pilota entra nella zona A
	tc.richAutorizDecolla(io);
	// il pilota entra nella zona B e libera la A
	tc.inVolo(io);
	//il pilota e' in volo e la zona B e' libera
    }

}

public class AereoCheAtterra extends Thread
{
    private int io;
    private TorreDiControllo tc;

    
    public AereoCheAtterra(int IO, TorreDiControllo torre)
    {
	this.io = IO;
	this.tc = torre;
    }

    public void run()
    {
	// il pilota e' in volo e deve attendere
	tc.richiAutorizAtterraggio(io);
	// il pilota atterra occupando la zona A
	tc.freniAttivati(io);
	// il pilota impegnala zona B e libera la A
	tc.inParcheggio(io);
	// il pilota esce dalla pista e libera B
    }
}

public class TorreDiControlloSemP extends TorreDiControllo 
{

    // condizioni iniziale: tutta la pista e' libera e' nessun aereo ha inoltrato richieste
    private Semaphore attesaA  = new Sempaphore(false); 
    private Sempaphore attesaB = new Sempaphore(false);
    private Semaphore attesaAtterraggio = new Semaphore(false);
    private Semaphore mutex = new Semaphore(true);
    
    public void richAccessoPista(int IO)
    {
	mutex.p();

	// se non trovo A libero o aerei che vogliono atterrare devo attendere
	if ( postiLiberiA == 0 || aereiInAtterraggio != 0 ) {
	    codaDecolloA++;
	    mutex.v();
	    attesaA.p();
	}

	// lascio l'attesa ed entro in A occupando un posto
	postiLiberiA--;
	codaDecolloA--;
	mutex.v();
	return;
    }
    
    public void richAutorizDecollo(int IO)
    {
	mutex.p();

	// se non trovo posti in B aspetto
	// non devo verificare che ci siano aeri che stanno atterrando visto che era una condizione necessaria per A
	if ( postiLiberiB == 0 ) {
	    codaDecolloB++;
	    mutex.v();
	    attesaB.p();
	}

	// se trovo libero entro in B
	codaDecolloB--;
	// lasciando un posto libero in A
	postiLiberiA++;
	// e mi preparo a decollare
	postiLiberiB--;
	mutex.v();
	return;
	    
    }
    
    public void inVolo(int IO)
    {
	mutex.p();

	// ho liberato un posto in B
	postiLiberiB++;
	
	// garantisco che in B c'e' la pista libera (automaticamente verificata)
	if ( postiLiberiB > 0)
	    attesaB.v();
	
	// garantisco che in A non ci siano aerei che vogliano atterrare e che ci siano posti liberi
	if ( postiLiberiA > 0 && aereiInAtterraggio == 0)
	    attesaA.v();

	// a questo punto la pista e' nelle condizioni iniziali
	mutex.v();
	return;
    }
    
    public void richAutorizAtterraggio(int IO)
    {
	mutex.p();

	// se non ho tutta la pista libera attendo
	if ( postiLiberiA < 2 or postiLiberi < 2 or atterraggiInCorso != 0 ) {
	    codaAtterraggio++;
	    mutex.v();
	    attesaAtterraggio.p();
	}

	// atterro in pista: non sono piu' in coda
	codaAtterraggio--;
	// segnalo che c'e' un aereo in atterraggio
	aereiInAtterraggio++;
	// occupo tutti i posti liberi in A
	postiLiberiA = 0;
	// e sono a posto per il momento
	mutex.v();
	
	return;
    }
	    
    public void freniAttivati(int IO)
    {
	mutex.p();

	// ho lasciato la zona A
	postiLiberiA = 2;
	// e sto occupando la zona B per frenare
	postiLiberiB = 0;

	mutex.v();
    }
    
    public void inParcheggio(int IO)
    {
	mutex.p();

	// ho lasciato la zona B
	postiLiberiB = 2;
	// e ho concluso il mio atterraggio
	aereiInAtterraggio--;
	// verifico prima se ho aerei che vogliono atterrare
	if ( codaAtterraggio > 0 && postiLiberiA == 2)
	    attesaAtterraggio.v();
	// altrimenti lascio la pista agli aerei che vogliono entrare in pista per il decollo
	if ( codaDecolloA > 0 && postiLiberiA != 0 )
	    attesaA.v();
	// altrimenti sono nelle condizioni iniziali e chiudo
	mutex.v();
	return;
	    
    }
}

public class TorreDiControlloHor extends TorreDiControllo
{
    private Monitor torre = new Monitor;
    
}
