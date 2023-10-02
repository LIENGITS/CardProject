package cs3500.klondike.model.hw02;

import java.util.List;

/**
 * KlondikeCard that represents a single card.
 */
public class KlondikeCard implements Card {
  /**
   * enum type to represent 4 types of suits.
   */
  public static enum Suits { CLUBS, DIAMONDS, HEARTS, SPADES }

  protected final Suits suit;
  protected final String value;

  // if the card is face-up (visible) or face-down
  private boolean visible;


  /**
   * KlondikeCard constructor.
   * @param suit the 4 types of suits
   * @param value card number and vaule
   */
  public KlondikeCard(Suits suit, String value) {
    if (suit == null) {
      throw new IllegalArgumentException("Suit shouldn't be null.");
    }
    if (!List.of("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q",
            "K").contains(value)) {
      throw new IllegalArgumentException("Invalid card value");
    }

    this.suit = suit;
    this.value = value;
    // car's default state is always face-down
    this.visible = false;

  }


  /**
   * Renders a card with its value followed by its suit as one of
   * the following symbols (♣, ♠, ♡, ♢).
   * For example, the 3 of Hearts is rendered as {@code "3♡"}.
   *
   * @return the formatted card
   */
  public String toString() {
    if (this.suit == null || this.value == null) {
      throw new IllegalArgumentException("Card's suit or value is null");
    }
    switch (suit) {
      case CLUBS:
        return value + "♣";
      case DIAMONDS:
        return value + "♢";
      case HEARTS:
        return value + "♡";
      case SPADES:
        return value + "♠";
      default:
        // This should never be reached if all possible suits are handled in the cases above
        throw new IllegalStateException("Unexpected card suit: " + suit);
    }
  }


  /**
   * check if two card are the same.
   * @param other the other card
   * @return true is same, and otherwise.
   */
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof KlondikeCard)) {
      return false;
    }
    KlondikeCard otherCard = (KlondikeCard) other;
    return this.suit == otherCard.suit && this.value.equals(otherCard.value);
  }

  /**
   * return the hash with of the card.
   * @return hash value
   */
  @Override
  public int hashCode() {
    return 31 * suit.hashCode() + value.hashCode();
  }


  /**
   * the String value of the card.
   * @return the car string value
   */
  @Override
  public String getRank() {
    return value;
  }


  /**
   * get the number value of the card.
   * @return the int value
   */
  @Override
  public int getRankValue() {
    switch (value) {
      case "A":
        return 1;
      case "2":
        return 2;
      case "3":
        return 3;
      case "4":
        return 4;
      case "5":
        return 5;
      case "6":
        return 6;
      case "7":
        return 7;
      case "8":
        return 8;
      case "9":
        return 9;
      case "10":
        return 10;
      case "J":
        return 11;
      case "Q":
        return 12;
      case "K":
        return 13;
      default:
        throw new IllegalArgumentException("Invalid rank: " + value);
    }
  }

  /**
   * get the card's suit.
   * @return the suit type of the card
   */
  @Override
  public Suits getSuit() {
    return suit;
  }


  /**
   * the String type of the color of the card.
   * @return the card color black or white
   */
  @Override
  public String getColor() {
    switch (suit) {
      case DIAMONDS:
      case HEARTS:
        return "Red";
      case CLUBS:
      case SPADES:
        return "Black";
      default:
        throw new IllegalArgumentException("Invalid suit: " + suit);
    }
  }

  /**
   * check if it is visible.
   * @return true if they are the same color
   */
  // This method returns if the card is face-up or face-down
  public boolean isVisible() {
    return visible;
  }

  /**
   * set the card to be visible.
   * @param visible ture if it is visible
   */
  // Setter method to set the visibility status of the card
  public void setVisible(boolean visible) {
    this.visible = visible;
  }


}

