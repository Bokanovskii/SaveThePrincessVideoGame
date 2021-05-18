/*
Viewport ideally helps control what part of the world we are looking at for drawing only what we see
Includes helpful helper functions to map between the viewport and the real world
 */


final class Viewport
{
   private int row;
   private int col;
   private final int numRows;
   private final int numCols;

   public Viewport(int numRows, int numCols)
   {
      this.numRows = numRows;
      this.numCols = numCols;
   }

   public void shift(int col, int row)
   {
      this.col = col;
      this.row = row;
   }

   public boolean contains(Point p)
   {
      return p.y >= row && p.y < row + numRows &&
              p.x >= col && p.x < col + numCols;
   }

   public int getRow()
   {
      return row;
   }
   public int getCol()
   {
      return col;
   }
   public int getNumRows()
   {
      return numRows;
   }
   public int getNumCols()
   {
      return numCols;
   }

   public Point viewportToWorld(int col, int row)
   {
      return new Point(col + this.getCol(), row + this.getRow());
   }
   public Point worldToViewport(int col, int row)
   {
      return new Point(col - this.getCol(), row - this.getRow());
   }

}
