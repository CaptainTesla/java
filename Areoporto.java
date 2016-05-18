/*
  per verificare la correttezza del codice:
  ad ogni incremento di variabile deve corrispondere nel flusso di esecuzione un corrispondente 
  decremento 
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
