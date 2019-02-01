package quizretakes;

/**
 * This bean holds a single quiz retake appointment

 * @author Jeff Offutt
 */

public class apptBean
{
   private final int quizID;
   private final int retakeID;
   private final String name;

   // *** Constructor *** //
   public apptBean (int retakeID, int quizID, String name)
   {
      this.retakeID = retakeID;
      this.quizID   = quizID;
      this.name     = name;
   }

   // *** Getters *** //
   public int getQuizID()
   {
      return quizID;
   }
   public int getRetakeID()
   {
      return retakeID;
   }
   public String getName()
   {
      return name;
   }

   public String toString()
   {
      return retakeID + ":" + quizID + ":" + name;
   }
}
