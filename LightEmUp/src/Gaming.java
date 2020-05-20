import java.util.ArrayList;
import java.util.Arrays;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;

import javalib.worldimages.*;

// Constants class
class Constants {

  static final int pieceWidth = 75;
  static final Color backgroundColor = Color.DARK_GRAY;
  static final Color brightestColor = Color.getHSBColor(55, 100, 100);
  static final Color powerCellColor = Color.CYAN;
  static final int pipeWidth = 5;

}

// Edge class
class Edge {
  GamePiece fromNode;
  GamePiece toNode;
  int weight;
}

// LightEmAll class for game
class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;

  // the current location of the power station,
  // as well as its effective radius
  int powerRow;
  int powerCol;
  int radius;



  public LightEmAll(
      ArrayList<ArrayList<GamePiece>> board, 
      ArrayList<GamePiece> nodes,
      ArrayList<Edge> mst,
      int powerRow, int powerCol, int radius) {
    this.board = board;
    this.nodes = nodes;
    this.mst = mst;
    this.width = board.get(0).size();
    this.height = board.size();
    this.powerRow = powerRow;
    this.powerCol = powerCol;
    this.radius = radius;

    // initialize neighbors
    for (ArrayList<GamePiece> row : this.board) {
      for (GamePiece piece : row) {
        piece.powerStation = piece.row == this.powerRow && piece.col == this.powerCol;
        piece.neighbors = this.getNeighbors(piece.row, piece.col);

        System.out.println(piece.neighbors.toString());

      }
    }

  }

  // Creates a list of neighbors for the given LightEmAll peice
  ArrayList<GamePiece> getNeighbors(int row, int col) {

    ArrayList<GamePiece> neighbors = new ArrayList<GamePiece>();

    try {
      neighbors.add(this.board.get(row + 1).get(col));
    }
    catch (IndexOutOfBoundsException e) {
      //empty
    }

    try {
      neighbors.add(this.board.get(row).get(col + 1));
    }
    catch (IndexOutOfBoundsException e) {
      //empty
    }

    try {
      neighbors.add(this.board.get(row).get(col - 1));
    }
    catch (IndexOutOfBoundsException e) {
      //empty
    }

    try {
      neighbors.add(this.board.get(row - 1).get(col));
    }
    catch (IndexOutOfBoundsException e) {
      //empty
    }

    return neighbors;

  }

  // On click method to rotate gamePiece clockwise once
  public void onMouseClicked(Posn mousePosn, String buttonName) {
    // TODO Auto-generated method stub
    super.onMouseReleased(mousePosn, buttonName);

    int x = mousePosn.x / Constants.pieceWidth;
    int y = mousePosn.y / Constants.pieceWidth;

    GamePiece piece = this.board.get(y).get(x);

    piece.rotate();

  }



  // Draw method to draw the gameboard
  public WorldScene makeScene() {

    double trueWidth = Constants.pieceWidth * this.width;
    double trueHeight = Constants.pieceWidth * this.height;

    WorldScene retScene = new WorldScene((int)trueWidth, (int)trueHeight);

    WorldImage finalImage = new EmptyImage();

    for (ArrayList<GamePiece> row : this.board) {
      WorldImage rowImage = new EmptyImage();
      for (GamePiece piece : row) {
        rowImage = new BesideImage(rowImage, piece.drawPiece());
      }
      finalImage = new AboveImage(finalImage, rowImage);
    }

    retScene.placeImageXY(finalImage, (int)trueWidth / 2, (int)trueHeight / 2);

    return retScene;

  }


}

// GamePiece class 
class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;

  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;

  // whether the power station is on this piece
  boolean powerStation;

  //what color the pipes in this piece are
  Color pipeColor;

  ArrayList<GamePiece> neighbors = new ArrayList<GamePiece>(); 

  public GamePiece(
      int row, int col, 
      boolean left, boolean right, boolean top, boolean bottom,
      boolean powerStation,
      Color pipeColor) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = powerStation;
    this.pipeColor = pipeColor;
  }

  // Rotates the given gamepiece clockwise once
  void rotate() {
    boolean tmpLeft = this.left;
    this.left = this.bottom;
    this.bottom = this.right;
    this.right = this.top;
    this.top = tmpLeft;
  }

  // Draw method for individual gamepieces
  WorldImage drawPiece() {
    WorldImage bg = new RectangleImage(
        Constants.pieceWidth,
        Constants.pieceWidth,
        OutlineMode.SOLID,
        Constants.backgroundColor);


    if (this.left) {
      bg = new RectangleImage(
          (Constants.pieceWidth) / 2, 
          Constants.pipeWidth,
          OutlineMode.SOLID,
          this.pipeColor).movePinhole(Constants.pieceWidth / 4, 0).overlayImages(bg);
    }

    if (this.right) {
      bg = new RectangleImage(
          (Constants.pieceWidth) / 2, 
          Constants.pipeWidth,
          OutlineMode.SOLID,
          this.pipeColor).movePinhole(- Constants.pieceWidth / 4, 0).overlayImages(bg);
    }

    if (this.top) {
      bg = new RectangleImage(
          Constants.pipeWidth,
          (Constants.pieceWidth) / 2, 
          OutlineMode.SOLID,
          this.pipeColor).movePinhole(0, Constants.pieceWidth / 4).overlayImages(bg);
    }

    if (this.bottom) {
      bg = new RectangleImage(
          Constants.pipeWidth,
          (Constants.pieceWidth) / 2, 
          OutlineMode.SOLID,
          this.pipeColor).movePinhole(0, - Constants.pieceWidth / 4).overlayImages(bg);
    }

    if (this.powerStation) {

      bg = new CircleImage(Constants.pieceWidth / 6,
          OutlineMode.SOLID,
          Constants.powerCellColor).overlayImages(bg);

    }

    bg = new RectangleImage(
        Constants.pieceWidth,
        Constants.pieceWidth,
        OutlineMode.OUTLINE,
        Color.BLACK).overlayImages(bg);

    return bg;

  }  
}

class ExampleGame {

  GamePiece x = new GamePiece(
      0, 
      0,
      false,
      true,
      false,
      true,
      false,
      Color.YELLOW);

  GamePiece y = new GamePiece(
      0, 
      1,
      true,
      true,
      false,
      true,
      false,
      Color.YELLOW);

  GamePiece z = new GamePiece(
      0, 
      2,
      true,
      false,
      false,
      true,
      false,
      Color.YELLOW);

  GamePiece a = new GamePiece(
      1, 
      0,
      false,
      true,
      true,
      true,
      false,
      Color.YELLOW);

  GamePiece a1 = new GamePiece(
      1, 
      0,
      true,
      false,
      true,
      true,
      false,
      Color.YELLOW);

  GamePiece b = new GamePiece(
      1, 
      1,
      true,
      true,
      true,
      true,
      false,
      Color.YELLOW);

  GamePiece c = new GamePiece(
      1, 
      2,
      true,
      false,
      true,
      true,
      false,
      Color.YELLOW);

  GamePiece h = new GamePiece(
      2, 
      0,
      false,
      true,
      true,
      false,
      false,
      Color.YELLOW);

  GamePiece i = new GamePiece(
      2, 
      1,
      true,
      true,
      true,
      false,
      false,
      Color.YELLOW);

  GamePiece j = new GamePiece(
      2, 
      2,
      true,
      false,
      true,
      false,
      false,
      Color.YELLOW);

  LightEmAll gameOne = new LightEmAll(
      new ArrayList<ArrayList<GamePiece>>(
          Arrays.asList(
              new ArrayList<GamePiece>(
                  Arrays.asList(this.x, this.y, this.z)),
              new ArrayList<GamePiece>(
                  Arrays.asList(this.a, this.b, this.c)),
              new ArrayList<GamePiece>(
                  Arrays.asList(this.h, this.i, this.j)))), null, null, 1, 1, 0);

  ArrayList<GamePiece> neighbor1 = new ArrayList<GamePiece>(
      Arrays.asList(this.y, this.a));

  WorldImage gpeice1 = new RectangleImage(
      Constants.pipeWidth,
      (Constants.pieceWidth) / 2, 
      OutlineMode.SOLID,
      Color.YELLOW).movePinhole(0, - Constants.pieceWidth / 4).overlayImages(new RectangleImage(
          (Constants.pieceWidth) / 2, 
          Constants.pipeWidth,
          OutlineMode.SOLID,
          Color.YELLOW).movePinhole(- Constants.pieceWidth / 4, 0).overlayImages(new RectangleImage(
              Constants.pieceWidth,
              Constants.pieceWidth,
              OutlineMode.SOLID,
              Constants.backgroundColor).overlayImages(new RectangleImage(
                  Constants.pieceWidth,
                  Constants.pieceWidth,
                  OutlineMode.OUTLINE,
                  Color.black))));

  WorldImage gamep1 = new RectangleImage(
      75,
      75, 
      OutlineMode.SOLID,
      Color.yellow).movePinhole(0, - Constants.pieceWidth / 4).overlayImages(new RectangleImage(
          (Constants.pieceWidth) / 2, 
          Constants.pipeWidth,
          OutlineMode.SOLID,
          Color.yellow).movePinhole(- Constants.pieceWidth / 4, 0).overlayImages(new RectangleImage(
              Constants.pieceWidth,
              Constants.pieceWidth,
              OutlineMode.SOLID,
              Constants.backgroundColor)));  

  WorldImage game1 = 
      new RectangleImage(
          (Constants.pieceWidth) / 2, 
          Constants.pipeWidth,
          OutlineMode.SOLID,
          Color.yellow)
      .movePinhole(Constants.pieceWidth / 4, 0).overlayImages(
          new RectangleImage(
              (Constants.pieceWidth) / 2, 
              Constants.pipeWidth,
              OutlineMode.SOLID,
              Color.yellow)
          .movePinhole(- Constants.pieceWidth / 4, 0)
          .overlayImages(new RectangleImage(
              Constants.pieceWidth,
              Constants.pieceWidth,
              OutlineMode.SOLID,
              Constants.backgroundColor)));

  void testGame(Tester t) {

    gameOne.bigBang(
        3 * Constants.pieceWidth,
        3 * Constants.pieceWidth,
        1.0 / 24);
  }

  void testRotate(Tester t) {

    t.checkExpect(x.left, false);
    t.checkExpect(x.top, false);
    t.checkExpect(x.right, true);
    t.checkExpect(x.bottom, true);

    x.rotate();

    t.checkExpect(x.left, true);
    t.checkExpect(x.top, false);
    t.checkExpect(x.right, false);
    t.checkExpect(x.bottom, true);

    x.rotate();

    t.checkExpect(x.left, true);
    t.checkExpect(x.top, true);
    t.checkExpect(x.right, false);
    t.checkExpect(x.bottom, false);

    x.rotate();

    t.checkExpect(x.left, false);
    t.checkExpect(x.top, true);
    t.checkExpect(x.right, true);
    t.checkExpect(x.bottom, false);

    x.rotate();

    t.checkExpect(x.left, false);
    t.checkExpect(x.top, false);
    t.checkExpect(x.right, true);
    t.checkExpect(x.bottom, true);
  }

  void testOnClick(Tester t) {
    this.gameOne.onMouseClicked(new Posn(3, 3), "left-click");


    t.checkExpect(x.left, true);
    t.checkExpect(x.top, false);
    t.checkExpect(x.right, false);
    t.checkExpect(x.bottom, true);


    this.gameOne.onMouseClicked(new Posn(3, 3), "left-click");
    this.gameOne.onMouseClicked(new Posn(3, 3), "left-click");
    this.gameOne.onMouseClicked(new Posn(3, 3), "left-click");

  }

  void testDraw(Tester t) {
    t.checkExpect(this.a.drawPiece(), this.game1);
  }
}