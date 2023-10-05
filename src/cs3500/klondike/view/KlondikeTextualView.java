package cs3500.klondike.view;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * A simple text-based rendering of the Klondike game.
 */
public class KlondikeTextualView implements TextView {
  protected KlondikeModel model;

  /**
   * KlondikeTextualView constructor.
   * @param model the KlondikeModel model
   */
  public KlondikeTextualView(KlondikeModel model) {
    this.model = model;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    // Draw Cards
    sb.append("Draw:");
    for (Card card : model.getDrawCards()) {
      sb.append(" ").append(card);
    }
    sb.append("\n");

    // Foundation Piles
    sb.append("Foundation:");
    for (int i = 0; i < model.getNumFoundations(); i++) {
      try {
        Card card = model.getCardAt(i, 0);  // Try to get the top card
        if (card != null) {
          sb.append(" ").append(card);
        } else {
          sb.append(" <none>");
        }
      } catch (IllegalArgumentException e) {
        sb.append(" <none>");
      }
    }
    sb.append("\n");

    // Cascade Piles
    int numPiles = model.getNumPiles();
    int maxPileHeight = model.getNumRows();

    for (int row = 0; row < maxPileHeight; row++) {
      for (int pileNum = 0; pileNum < numPiles; pileNum++) {
        if (row < model.getPileHeight(pileNum)) {
          Card card = model.getCardAt(pileNum, row);
          if (card == null) {
            sb.append("  ?");
          } else if (card.isVisible()) {
            sb.append(String.format("%3s", card));
          } else {
            sb.append("  ?");
          }
        } else {
          sb.append("   "); // Three spaces for alignment with the card's representation.
        }
      }
      sb.append("\n");
    }

    return sb.toString();
  }




}
