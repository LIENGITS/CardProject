package cs3500.klondike.model.hw02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is a representation of the game that store the current state of the game. After it
 * implements the interface, it can also store the state after starting the game.
 * BasicKlondike will have a zero-argument (i.e. default) constructor,
 * and that all the methods below will be implemented.
 *
 * <p>Once you've implemented all the constructors and methods on your own, you can
 * delete the placeholderWarning() method.
 */
public class BasicKlondike implements KlondikeModel {

  //missing private fields
  protected List<Card> deck;

  //pile's current list of lists of cards
  protected List<List<Card>> cascade;

  protected List<List<Card>> foundations;

  protected List<Card> drawPile;

  // Add a new field at the class level to store the rest of the cards
  protected List<Card> restOfDeck;

  //rule of the draw number of the game
  protected int numDraw;
  protected int numPiles;

  protected boolean isGameStarted;

  /**
   * the initialization of the BasicKlondike constructor.
   */
  public BasicKlondike() {

    // Generate a default deck
    this.deck = generateDefaultDeck();

    // Check if the deck is valid
    if (!isValidDeck(deck)) {
      throw new IllegalArgumentException("Generated deck is invalid!");
    }

    if (numPiles < 0 || numDraw < 0) {
      throw new IllegalArgumentException("Pile number and draw number cannot be negative.");
    }

    foundations = new ArrayList<>();

    this.numPiles = numPiles;
    cascade = new ArrayList<>();

    this.numDraw = numDraw;

    // Initialize the empty drawPile
    drawPile = new ArrayList<>();

    // Initialize restOfDeck
    restOfDeck = new ArrayList<>();

    this.isGameStarted = false;

  }

  private static List<Card> generateDefaultDeck() {

    List<Card> deck = new ArrayList<>();

    KlondikeCard.Suits[] suits = KlondikeCard.Suits.values();
    String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    for (KlondikeCard.Suits suit : suits) {
      for (String value : values) {
        deck.add(new KlondikeCard(suit, value));
      }
    }
    return deck;
  }


  private boolean isValidDeck(List<Card> deck) {

    if (deck == null) {
      return false; // Handle the case of a null deck
    }

    // Check each card in the deck for null.
    for (Card card : deck) {
      if (card == null) {
        return false; // Handle the case of a null card in the deck
      }
    }

    // Group cards by suit
    Map<KlondikeCard.Suits, List<Card>> groupedBySuit = deck.stream()
            .collect(Collectors.groupingBy(card -> ((KlondikeCard) card).getSuit()));

    int runLength = -1;

    for (List<Card> suitCards : groupedBySuit.values()) {
      // Sort cards within a suit by their rank values
      suitCards.sort(Comparator.comparingInt(Card::getRankValue));

      // Check if the first card of this suit is an Ace
      if (!suitCards.get(0).getRank().equals("A")) {
        return false;
      }

      // If the runLength has not been set yet (i.e., this is the first suit we're checking),
      // set it to the length of this suit's run
      if (runLength == -1) {
        runLength = suitCards.size();
      }

      // If the current suit's run length doesn't match the length of the first suit's run,
      // return false
      if (suitCards.size() != runLength) {
        return false;
      }

      // Check if the cards form a consecutive sequence
      for (int i = 1; i < suitCards.size(); i++) {
        if (suitCards.get(i).getRankValue() != suitCards.get(i - 1).getRankValue() + 1) {
          return false;
        }
      }
    }

    return true;
  }


  /**
   * Return a valid and complete deck of cards for a game of Klondike.
   * There is no restriction imposed on the ordering of these cards in the deck.
   * The validity of the deck is determined by the rules of the specific game in
   * the classes implementing this interface.  This method may be called as often
   * as desired.
   *
   * @return the deck of cards as a list
   */
  @Override
  public List<Card> getDeck() {
    if (deck == null) {
      return new ArrayList<>(); // Return an empty list if deck is null
    }
    List<Card> shuffledDeck = new ArrayList<>(deck);
    Collections.shuffle(shuffledDeck);
    return shuffledDeck;
  }



  /**
   * <p>Deal a new game of Klondike.
   * The cards to be used and their order are specified by the the given deck,
   * unless the {@code shuffle} parameter indicates the order should be ignored.</p>
   *
   * <p>This method first verifies that the deck is valid. It deals cards in rows
   * (left-to-right, top-to-bottom) into the characteristic cascade shape
   * with the specified number of rows, followed by (at most) the specified number of
   * draw cards. When {@code shuffle} is {@code false}, the {@code deck} must be used in
   * order and the 0th card in {@code deck} is used as the first card dealt.
   * There will be as many foundation piles as there are Aces in the deck.</p>
   *
   * <p>A valid deck must consist cards that can be grouped into equal-length,
   * consecutive runs of cards (each one starting at an Ace, and each of a single
   * suit).</p>
   *
   * <p>This method should have no side effects other than configuring this model
   * instance, and should work for any valid arguments.</p>
   *
   * @param deck     the deck to be dealt
   * @param shuffle  if {@code false}, use the order as given by {@code deck},
   *                 otherwise use a randomly shuffled order
   * @param numPiles number of piles to be dealt
   * @param numDraw  maximum number of draw cards available at a time
   * @throws IllegalStateException    if the game has already started
   * @throws IllegalArgumentException if the deck is null or invalid,
   *                                  a full cascade cannot be dealt with the given sizes,
   *                                  or another input is invalid
   */
  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
          throws IllegalArgumentException {

    if (isGameStarted) {
      throw new IllegalStateException("The game has already started.");
    }

    if (deck == null || deck.isEmpty()) {
      throw new IllegalArgumentException("Deck is null or invalid");
    }

    Set<Card> cardSet = new HashSet<>(deck);
    if (cardSet.size() != deck.size()) {
      throw new IllegalArgumentException("Deck contains duplicate cards");
    }


    if (numPiles < 0 || numDraw < 0) {
      throw new IllegalArgumentException("Invalid number of cascade piles.");
    }

    // Check for the relationship between deck.size(), numPiles, and numDraw
    if (deck.size() < ((numPiles * (numPiles + 1)) / 2)) {
      throw new IllegalArgumentException("Not enough cards in deck for the given number of " +
              "piles and draw cards.");
    }


    if (!isValidDeck(deck)) {
      throw new IllegalArgumentException("Provided deck is not valid for Klondike");
    }


    if (shuffle) {
      Collections.shuffle(deck);
    }

    this.deck = new ArrayList<>(deck);
    this.numPiles = numPiles;
    this.numDraw = numDraw;

    // Reset cascade and foundation piles
    cascade.clear();
    for (int i = 0; i < numPiles; i++) {
      cascade.add(new ArrayList<>());
    }

    // Initialize foundations based on the number of Aces in the deck
    foundations.clear();
    int aceCount = (int) deck.stream().filter(card -> ((KlondikeCard) card).getRank().
            equals("A")).count();
    for (int i = 0; i < aceCount; i++) {
      foundations.add(new ArrayList<>());
    }

    // Deal deck into cascade piles
    int cardIndex = 0;
    for (int pileNumber = 0; pileNumber < numPiles; pileNumber++) {
      for (int cardNumberInPile = 0; cardNumberInPile <= pileNumber; cardNumberInPile++) {
        if (cardIndex >= deck.size()) {
          throw new IllegalArgumentException("Not enough cards to deal");
        }
        KlondikeCard card = (KlondikeCard) deck.get(cardIndex);
        // set the visibility of the cards; only the top card of each cascade pile is visible
        card.setVisible(cardNumberInPile == pileNumber);
        cascade.get(pileNumber).add(card);
        cardIndex++;
      }
    }

    // Reset the drawPile and restOfDeck
    drawPile.clear();
    restOfDeck = new ArrayList<>();

    // Deal the next 'numDraw' cards to drawPile
    for (int i = 0; i < Math.min(numDraw, deck.size() - cardIndex); i++) {
      KlondikeCard card = (KlondikeCard) deck.get(cardIndex++);
      card.setVisible(true);
      drawPile.add(card);
    }

    // Deal the remaining cards to restOfDeck
    while (cardIndex < deck.size()) {
      KlondikeCard card = (KlondikeCard) deck.get(cardIndex++);
      card.setVisible(false);
      restOfDeck.add(card);
    }
    this.isGameStarted = true;
  }


  /**
   * Moves the requested number of cards from the source pile to the destination pile,
   * if allowable by the rules of the game.
   *
   * @param srcPile  the 0-based index (from the left) of the pile to be moved
   * @param numCards how many cards to be moved from that pile
   * @param destPile the 0-based index (from the left) of the destination pile for the
   *                 moved cards
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if either pile number is invalid, if the pile
   *                                  numbers are the same, or there are not enough cards to from
   *                                  the srcPile to the destPile (i.e. the move is not physically
   *                                  possible)
   * @throws IllegalStateException    if the move is not allowable (i.e. the move is not
   *                                  logically possible)
   */
  @Override
  public void movePile(int srcPile, int numCards, int destPile) {
    ensureGameStarted();

    if (srcPile == destPile || srcPile < 0 || srcPile >= cascade.size() || destPile < 0 ||
            destPile >= cascade.size()) {
      throw new IllegalArgumentException("Invalid pile numbers provided");
    }

    List<Card> sourceCards = cascade.get(srcPile);
    List<Card> destCards = cascade.get(destPile);

    if (sourceCards.size() < numCards) {
      throw new IllegalArgumentException("Not enough cards in source pile to move");
    }

    Card movingCard = sourceCards.get(sourceCards.size() - numCards);

    // Prevent the movement of a card with rank "A" to another cascade pile.
    if (movingCard.getRank().equals("A")) {
      throw new IllegalStateException("Aces can only be moved to the foundation pile");
    }

    Card topDestCard = destCards.isEmpty() ? null : destCards.get(destCards.size() - 1);

    if (topDestCard == null) {
      if (movingCard.getRankValue() != getMaxRankValue()) {
        throw new IllegalStateException("Only the highest rank card can be placed on an " +
                "empty pile");
      }
    } else {
      // Ensure the card being moved is one less in rank than the card it's being placed on
      if (movingCard.getRankValue() + 1 != topDestCard.getRankValue()) {
        throw new IllegalStateException("Card being moved does not follow in sequence");
      }

      // Ensure cards alternate in color
      if (movingCard.getColor().equals(topDestCard.getColor())) {
        throw new IllegalStateException("Card being moved must be opposite in color");
      }
    }

    // Move the cards
    List<Card> movingCards = new ArrayList<>(sourceCards.subList(sourceCards.size() -
            numCards, sourceCards.size()));
    sourceCards.removeAll(movingCards);
    destCards.addAll(movingCards);

    // If there's a card left in the source pile, make its top card visible
    if (!sourceCards.isEmpty()) {
      KlondikeCard topSourceCard = (KlondikeCard) sourceCards.get(sourceCards.size() - 1);
      topSourceCard.setVisible(true);
    }

  }

  /**
   * Moves the topmost draw-card to the destination pile.  If no draw cards remain,
   * reveal the next available draw cards
   *
   * @param destPile the 0-based index (from the left) of the destination pile for the
   *                 card
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if destination pile number is invalid
   * @throws IllegalStateException    if there are no draw cards, or if the move is not
   *                                  allowable
   */
  @Override
  public void moveDraw(int destPile) throws IllegalStateException {
    ensureGameStarted();

    if (destPile < 0 || destPile > (getNumPiles() - 1)) {
      throw new IllegalArgumentException("Invalid destination pile provided.");
    }

    if (drawPile.isEmpty()) {
      if (restOfDeck.isEmpty()) {
        throw new IllegalStateException("No draw cards available");
      }
      drawFromDeck();
    }

    //to get the first card of the draw pile
    Card topDrawCard = drawPile.get(0);

    // Assuming destPile from 0 to numPiles-1 is for cascade and destPile >=
    // numPiles is for foundation
    if (destPile < numPiles) {
      List<Card> destCards = cascade.get(destPile);
      Card topDestCard = destCards.isEmpty() ? null : destCards.get(destCards.size() - 1);

      // Check if move to cascade pile is valid
      if (!isValidMoveToCascade(topDrawCard, topDestCard)) {
        throw new IllegalStateException("Invalid move to cascade: Tried to move " +
                topDrawCard + " on top of " + topDestCard);
      }
      // If valid, move the card
      destCards.add(topDrawCard);
    } else {
      List<Card> foundationPile = foundations.get(destPile - numPiles);

      // Simply move the card to the foundation pile without any checks
      foundationPile.add(topDrawCard);
    }

    // Remove the card from draw pile
    drawPile.remove(topDrawCard);

    // If draw pile is empty after removing, refill from restOfDeck if any cards are left
    if (drawPile.isEmpty() && !restOfDeck.isEmpty()) {
      drawFromDeck();
    } else if (!drawPile.isEmpty()) {
      KlondikeCard nextDrawCard = (KlondikeCard) drawPile.get(0);
      nextDrawCard.setVisible(true);
    }
  }


  private void drawFromDeck() {
    // Clear the current draw pile
    drawPile.clear();
    // Move the next 'numDraw' cards from restOfDeck to drawPile
    for (int i = 0; i < Math.min(numDraw, restOfDeck.size()); i++) {
      KlondikeCard card = (KlondikeCard) restOfDeck.get(0);
      card.setVisible(true);
      drawPile.add(card);
      restOfDeck.remove(0);
    }
  }


  // If there's no card in the destination pile, the movingCard has to be the highest value of
  // deck, for example if my deck is consist of one set of each suit from Ace through Five,
  // then the movingCard to an empty DestCard has to be a 5. If the deck is consist of
  // one set of each suit from Ace through King, then the movingCard needs to be K.
  private boolean isValidMoveToCascade(Card movingCard, Card topDestCard) {
    // Prevent the movement of a card with rank "A" to another cascade pile.
    if (movingCard.getRank().equals("A")) {
      return false;
    }
    // If there's no card in the destination pile, the movingCard has to be the highest value of
    // deck, for example if my deck is consist of one set of each suit from Ace through Five,
    // then the movingCard to an empty DestCard has to be a 5. If the deck is consist of
    // one set of each suit from Ace through King, then the movingCard needs to be K.
    if (topDestCard == null) {
      return movingCard.getRankValue() == getMaxRankValue();
    }
    // If there is a card in the destination pile,
    // check if movingCard rank is one less than the top card and their colors are different
    return movingCard.getRankValue() + 1 == topDestCard.getRankValue()
            && !movingCard.getColor().equals(topDestCard.getColor());
  }

  /**
   * Returns the maximum rank value in the deck.
   */
  private int getMaxRankValue() {
    int maxRank = 0;
    for (Card card : deck) {
      if (card.getRankValue() > maxRank) {
        maxRank = card.getRankValue();
      }
    }
    return maxRank;
  }

  private boolean isValidMoveToFoundation(Card movingCard, Card topDestCard) {
    // If there's no card in the destination pile, return false
    if (topDestCard == null) {
      return movingCard.getRank().equals("A");
    }
    // If there is a card in the destination pile,
    // check if movingCard rank is one value more than the top card in foundation piles
    // and their colors are different


    return movingCard.getRankValue() == topDestCard.getRankValue() + 1 &&
            movingCard.getSuit() == topDestCard.getSuit();

  }

  private int numberOfAcesInDeck() {
    int count = 0;
    for (Card card : deck) {
      if (card.getRank().equals("A")) {
        count++;
      }
    }
    return count;
  }

  /**
   * Moves the top card of the given pile to the requested foundation pile.
   *
   * @param srcPile        the 0-based index (from the left) of the pile to move a card
   * @param foundationPile the 0-based index (from the left) of the foundation pile to
   *                       place the card
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if either pile number is invalid
   * @throws IllegalStateException    if the source pile is empty or if the move is not
   *                                  allowable
   */
  @Override
  public void moveToFoundation(int srcPile, int foundationPile)
          throws IllegalStateException {

    ensureGameStarted();

    // Check for valid pile numbers
    if (srcPile < 0 || srcPile >= numPiles || foundationPile < 0 || foundationPile >=
            numberOfAcesInDeck()) {
      throw new IllegalArgumentException("Invalid pile number");
    }

    // Check if source pile has cards
    List<Card> sourceCards = cascade.get(srcPile);
    if (sourceCards.isEmpty()) {
      throw new IllegalStateException("Source pile is empty");
    }

    // Get the top card of the source pile
    Card topSourceCard = sourceCards.get(sourceCards.size() - 1);

    // Get the foundation pile
    List<Card> foundationCards = foundations.get(foundationPile);
    Card topFoundationCard = foundationCards.isEmpty() ? null :
            foundationCards.get(foundationCards.size() - 1);

    // Check if move is valid
    if (!isValidMoveToFoundation(topSourceCard, topFoundationCard)) {
      throw new IllegalStateException("Invalid move to foundation");
    }

    // Move the card
    foundationCards.add(topSourceCard);
    sourceCards.remove(sourceCards.size() - 1);

    // If another card in the source pile after removing the top one,
    // make it visible
    if (!sourceCards.isEmpty()) {
      KlondikeCard nextTopCard = (KlondikeCard) sourceCards.get(sourceCards.size() - 1);
      nextTopCard.setVisible(true);
    }
  }


  /**
   * Moves the topmost draw-card directly to a foundation pile.
   *
   * @param foundationPile the 0-based index (from the left) of the foundation pile to
   *                       place the card
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if the foundation pile number is invalid
   * @throws IllegalStateException    if there are no draw cards or if the move is not
   *                                  allowable
   */
  @Override
  public void moveDrawToFoundation(int foundationPile) throws IllegalStateException {
    // Check if the game has started
    ensureGameStarted();

    // Check for valid foundation pile number, numbers of foundation should determine by nums Aces
    if (foundationPile < 0 || foundationPile >= numberOfAcesInDeck()) {
      throw new IllegalArgumentException("Invalid foundation pile number");
    }

    // Check if there are draw cards available
    if (drawPile.isEmpty()) {
      // Check if there are still cards left in restOfDeck
      if (restOfDeck.isEmpty()) {
        throw new IllegalStateException("No draw cards available");
      }
      drawFromDeck();
    }

    // Get the topmost draw card
    Card topDrawCard = drawPile.get(0);

    // Get the foundation pile
    List<Card> foundationCards = foundations.get(foundationPile);
    Card topFoundationCard = foundationCards.isEmpty() ? null :
            foundationCards.get(foundationCards.size() - 1);

    // Check if move is valid
    if (!isValidMoveToFoundation(topDrawCard, topFoundationCard)) {
      throw new IllegalStateException("Invalid move to foundation");
    }

    // Move the card
    foundationCards.add(topDrawCard);
    drawPile.remove(0);

    // If draw pile is empty after removing, refill from restOfDeck if any cards are left
    if (drawPile.isEmpty() && !restOfDeck.isEmpty()) {
      drawFromDeck();
    } else if (!drawPile.isEmpty()) {
      KlondikeCard nextDrawCard = (KlondikeCard) drawPile.get(0);
      nextDrawCard.setVisible(true);
    }
  }


  /**
   * Discards the topmost draw-card and moves it to the bottom of the drawPile.
   *
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalStateException if move is not allowable
   */
  @Override
  public void discardDraw() throws IllegalStateException {
    // Check if the game has started
    ensureGameStarted();

    // Check if there are draw cards available
    if (drawPile.isEmpty()) {
      throw new IllegalStateException("No draw cards available to discard");
    }

    // Move the topmost draw card to the bottom of the drawPile
    Card discardedCard = drawPile.remove(0);
    drawPile.add(discardedCard);

    // Update visibility of the next numDraw cards
    for (int i = 0; i < Math.min(numDraw, drawPile.size()); i++) {
      KlondikeCard card = (KlondikeCard) drawPile.get(i);
      card.setVisible(true);
    }
  }

  /**
   * At any point during the game, the cascade piles “fit” into a rectangle
   * that is getNumPiles() piles wide and getNumRows() rows tall.
   * Each pile is three characters wide, and cards are right-aligned within that column.
   * (Each row of the display should therefore be exactly 3 * getNumPiles() characters wide.)
   * Returns the number of rows currently in the game.
   *
   * @return the height of the current table of cards
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumRows() {
    // Check if the game has started
    ensureGameStarted();

    // Find the maximum height among all the cascade piles
    int maxRows = 0;
    for (List<Card> pile : cascade) {
      if (pile.size() > maxRows) {
        maxRows = pile.size();
      }
    }
    return maxRows;
  }

  /**
   * At any point during the game, the cascade piles “fit” into a rectangle
   * that is getNumPiles() piles wide and getNumRows() rows tall.
   *
   * @return the number of piles
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumPiles() {
    // Check if the game has started
    ensureGameStarted();

    // Return the number of cascade piles
    return cascade.size();
  }

  /**
   * returns the maximum number of draw cards visible at any time. The actual draw cards
   * visible at any time can be retrieved with getDrawCards().
   *
   * @return the number of visible cards in the draw pile
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumDraw() {
    ensureGameStarted();

    return numDraw;
  }

  /**
   * Signal if the game is over or not.  A game is over if there are no more
   * possible moves to be made, or draw cards to be used (or discarded).
   *
   * @return true if game is over, false otherwise
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public boolean isGameOver() throws IllegalStateException {
    // Check if the game has started
    ensureGameStarted();

    // Check if there are no more draw cards to be used or discarded
    if (drawPile.isEmpty()) {
      return true;
    }

    // Check for any possible moves among the cascade piles
    for (int i = 0; i < cascade.size(); i++) {
      List<Card> currentPile = cascade.get(i);
      if (currentPile.isEmpty()) {
        continue;
      }

      Card topCard = currentPile.get(currentPile.size() - 1);

      // Check for possible moves to other cascade piles or foundation piles
      for (int j = 0; j < cascade.size(); j++) {
        if (i == j) {
          continue;
        }

        List<Card> targetPile = cascade.get(j);
        // There's a move to an empty cascade pile with a King.
        if (targetPile.isEmpty() && topCard.getRankValue() == getMaxRankValue()) {
          return false;
          // There's a valid move to another cascade pile.
        } else if (!targetPile.isEmpty() && isValidMoveToCascade(topCard,
                targetPile.get(targetPile.size() - 1))) {
          return false;
        }
      }

      // If possible moves to foundation piles
      for (List<Card> foundationPile : foundations) {
        //There's a move to an empty foundation pile with an Ace.
        if (foundationPile.isEmpty() && topCard.getRank().equals("A")) {
          return false;
          // There's a valid move to a foundation pile.
        } else if (!foundationPile.isEmpty() && isValidMoveToFoundation(topCard,
                foundationPile.get(foundationPile.size() - 1))) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Return the current score, which is the sum of the values of the cards
   * in the foundation piles.
   *
   * @return the score
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getScore() throws IllegalStateException {
    // Check if the game has started
    ensureGameStarted();

    int score = 0;

    for (List<Card> foundationPile : foundations) {
      for (Card card : foundationPile) {
        score += card.getRankValue();
      }
    }

    return score;
  }

  /**
   * Returns the number of cards in the specified pile.
   * It is the current height of the given cascade pile.
   * Naturally, it must always be a value between 0 and getNumRows().
   *
   * @param pileNum the 0-based index (from the left) of the pile
   * @return the number of cards in the specified pile
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if pile number is invalid
   */
  @Override
  public int getPileHeight(int pileNum) throws IllegalStateException {
    // if the game has started
    ensureGameStarted();

    // if the given pileNum is valid
    if (pileNum < 0 || pileNum > (getNumPiles() - 1)) {
      throw new IllegalArgumentException("Pile number is invalid");
    }

    return cascade.get(pileNum).size();
  }

  /**
   * Returns whether the card at the specified coordinates is face-up or not.
   *
   * @param pileNum column of the desired card (0-indexed from the left)
   * @param card    row of the desired card (0-indexed from the top)
   * @return whether the card at the given position is face-up or not
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  @Override
  public boolean isCardVisible(int pileNum, int card) throws IllegalStateException {
    // Check if the game has started
    ensureGameStarted();

    // Check if the given pileNum is valid
    if (pileNum < 0 || pileNum >= numPiles) {
      throw new IllegalArgumentException("Invalid pile number");
    }


    List<Card> selectedPile = cascade.get(pileNum);
    if (selectedPile.isEmpty()) {
      throw new IllegalArgumentException("The specified pile is empty");
    }



    // Check if the card index is valid
    if (card < 0 || card >= cascade.get(pileNum).size()) {
      throw new IllegalArgumentException("Invalid card index");
    }

    // Retrieve the card at the specified position and return its visibility status
    Card selectedCard = cascade.get(pileNum).get(card);
    return selectedCard.isVisible();
  }


  /**
   * Returns the card at the specified coordinates, if it is visible.
   * returns the card at the given coordinates, if it is visible.
   * To check if it is visible, use isCardVisible with the same arguments.
   * Return null, if there is an existing card and it is not visible.
   *
   * @param pileNum column of the desired card (0-indexed from the left)
   * @param card    row of the desired card (0-indexed from the top)
   * @return the card at the given position, or <code>null</code> if no card is there
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  @Override
  public Card getCardAt(int pileNum, int card) throws IllegalStateException {
    // Check if the game has started
    ensureGameStarted();

    // Check if the given pileNum is valid
    if (pileNum < 0 || pileNum >= numPiles) {
      throw new IllegalArgumentException("Invalid pile number");
    }

    List<Card> selectedPile = cascade.get(pileNum);

    // Check if the pile has cards
    if (selectedPile.isEmpty()) {
      throw new IllegalArgumentException("The specified pile has no cards");
    }

    // Check if the card index is valid
    if (card < 0 || card >= selectedPile.size()) {
      throw new IllegalArgumentException("Card index out of bounds for the given pile");
    }

    // Check if the card is visible using the isCardVisible method
    if (!isCardVisible(pileNum, card)) {
      // Throw an exception if the card is not visible
      throw new IllegalArgumentException("Card at specified coordinates is not visible");
    }

    // If the card is visible, return it
    return selectedPile.get(card);
  }

  /**
   * Returns the card at the top of the specified foundation pile.
   * returns the card at the given coordinates, if it is visible.
   * To check if it is visible, use isCardVisible with the same arguments.
   * Return null, if there is an existing card and it is not visible.
   *
   * @param foundationPile 0-based index (from the left) of the foundation pile
   * @return the card at the given position, or <code>null</code> if no card is there
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if the foundation pile number is invalid
   */
  @Override
  public Card getCardAt(int foundationPile) throws IllegalStateException {

    ensureGameStarted();
    // if a invalid foundation pile  throw an exception
    if (foundationPile < 0 || foundationPile >= numberOfAcesInDeck()) {
      throw new IllegalArgumentException("Invalid foundation pile number");
    }

    List<Card> selectedFoundation = foundations.get(foundationPile);


    if (selectedFoundation.isEmpty()) {
      // Return null for an empty pile instead of throwing an exception
      return null;
    }

    Card topCard = selectedFoundation.get(selectedFoundation.size() - 1);

    // Check if the card is visible
    if (topCard.isVisible()) {
      return topCard;
    }

    // If the card is not visible, return null
    return null;
  }

  /**
   * Refreshes the draw pile from the remaining deck.
   */
  private void drawPile() {
    // Clear the current draw pile
    drawPile.clear();

    // If the deck has fewer cards than numDraw, take all the remaining cards
    int cardsToDraw = Math.min(deck.size(), numDraw);

    // Take from the end of the deck
    for (int i = 0; i < cardsToDraw; i++) {
      drawPile.add(deck.remove(deck.size() - 1));
    }
  }

  /**
   * Returns the currently available draw cards.
   * The actual draw cards visible at any time can be retrieved with getDrawCards().
   *
   * @return the ordered list of available draw cards
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public List<Card> getDrawCards() throws IllegalStateException {
    // Check if the game has started
    ensureGameStarted();

    if (drawPile == null) {
      throw new IllegalStateException("Draw pile is not initialized.");
    }
    // Return all cards from the draw pile
    return Collections.unmodifiableList(drawPile);
  }

  /**
   * Return the number of foundation piles in this game.
   * But the numbers of foundation of our game is set by the number of Aces
   *
   * @return the number of foundation piles
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumFoundations() throws IllegalStateException {
    ensureGameStarted();
    if (foundations == null) {
      throw new IllegalStateException("Foundations list is not initialized.");
    }
    return foundations.size();
  }


  private void ensureGameStarted() {
    if (!isGameStarted || this.deck == null || this.deck.isEmpty()) {
      throw new IllegalStateException("Game hasn't been started yet");
    }
  }

}
