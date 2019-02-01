package quizretakes;

// import java.io.Serializable; ?? Needed?
import quizretakes.bean.QuizBean;

import java.util.*;

/**
 * This class holds a collection of quizzes

 * @author Jeff Offutt
 */

public class quizzes implements Iterable<QuizBean>
{
   private final ArrayList<QuizBean> quizzes;

   // ***** Constructors //
   public quizzes ()
   {
      quizzes = new ArrayList<>();
   }

   public quizzes (int quizID, int month, int day, int hour, int minute)
   {  // Adds one quiz to a new list
      quizzes = new ArrayList<>();
      QuizBean qb = new QuizBean(quizID, month, day, hour, minute);
      quizzes.add (qb);
   }

   public quizzes (QuizBean qb)
   {
      quizzes = new ArrayList<>();
      quizzes.add (qb);
   }

   // *** sorting and iterating *** //
   public void sort ()
   {
      Collections.sort (quizzes);
   }

   @Override
   public Iterator<QuizBean> iterator()
   {
       return quizzes.iterator();
   }

   // ***** setters & getters //
   public void addQuiz (QuizBean qb)
   {
      quizzes.add (qb);
   }

   public String toString ()
   {
      return (Arrays.toString(quizzes.toArray()));
   }

}
