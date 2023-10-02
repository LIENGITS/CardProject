package cs3500.klondike.view;

import java.io.IOException;

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
    // Assuming that model provides a getDrawCards() method
    for (Card card : model.getDrawCards()) {
      sb.append(" ").append(card);
    }
    sb.append("\n");

    // Foundation Piles
    sb.append("Foundation:");
    // Using the getCardAt method for foundation piles now
    // foundation piles is equals the numbers of Aces, calling the getNumFoundations()
    //after the gameStart should return us the correct number of foundation
    for (int i = 0; i < model.getNumFoundations(); i++) {
      Card card = model.getCardAt(i, 0); // foundation piles only have one card at a time
      if (card == null) {
        sb.append(" <none>");
      } else {
        sb.append(" ").append(card);
      }
    }
    sb.append("\n");

    // Cascade Piles
    int numPiles = model.getNumPiles();
    int maxPileHeight = model.getNumRows();

    for (int row = 0; row < maxPileHeight; row++) {
      for (int pileNum = 0; pileNum < numPiles; pileNum++) {
        // Before getting the card, check if the index is valid
        if (row < model.getPileHeight(pileNum)) {
          Card card = model.getCardAt(pileNum, row);
          if (card == null) {
            sb.append("  ?");
          } else {
            sb.append(String.format("%3s", card));
          }
        } else {
          if (row == 0 && model.getPileHeight(pileNum) == 0) {
            sb.append("  X");
          } else {
            sb.append("   ");
          }
        }
      }
      sb.append("\n");
    }

    return sb.toString().trim();
  }

  @Override
  public void render() throws IOException {

  }
}
