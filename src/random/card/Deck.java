package random.card;

public interface Deck {
	public void shuffle();
	public void shuffleUnUsedDeck();
	public int getNextCard();
	public void putCardsBackInDeck();
	public boolean hasCards();
}
