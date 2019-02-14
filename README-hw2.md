# SWE-437 :: Quiz Scheduler

_(This is a copy of the readme as it was when assignment 2 was submitted)_

The [second assignment](https://cs.gmu.edu/~offutt/classes/437/assigns/assign02.html) for [SWE-437](https://cs.gmu.edu/~offutt/classes/437/index.html) on Software Evolution. 

![preview](preview-hw2.gif)

As shown in the preview, to register for a quiz retake, select both a quiz _(green)_ and a retake session _(blue)_. Then enter your name in the textfield and click _"Register"_ to sign up for the retake. A prompt will alert you to the success of your appointment.

## Requirements

* [Maven](https://maven.apache.org/) - Compilation
* [Java 8](https://openjdk.java.net/projects/jdk8/) - JavaFX

## Maintainability 

The following pros & cons pertain to the _initial state_ of the software. Some of the original files were excluded from the initial commit (`Makefile`/`deploy`), but may be found in the [original assignment zip](original.zip).

**Pros:**

* Small project, easy to comprehend
    * Additional comments helped to compartmentalize logic & provide concise summaries of components
* Logic was clearly separated among classes _(front-end-servlet / utilities / beans)_

**Cons:**

* Expected type of `daysAvailable` is documented to be an `int` but is defined as `String` in the code.
* Build/deploy systems were system-dependent
    * `Makefile` did not work out-of-the-box with a fresh installation of JDK-8
        * Dependencies _(javax.servlet)_ needed to be manually specified by the user in order to compile
    * `deploy` specified system-dependent paths & required setting up [additional software](https://piazza.com/class/jqwfp37y1ap78x?cid=30) to test
* Source code format did not follow [guidelines](https://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html) consistently between classes

**Redo-Assessment:**

I would not do anything differently while redoing the assignment. 

## Documentation log

* Replace build system
    * Delete `Makefile` and `deploy` 
    * Use [Maven](https://maven.apache.org/) instead
        * Automatically produces runnable jar with `mvn package`
* Clean up & architecture changes 
    * Remove unused imports
    * Remove unused constructors in bean classes
    * Auto-format all classes with consistent style guidelines
    * Rename classes to use CamelCasing instead of lowerCase
    * Remove debugging from reader classes
    * Move reader class methods into a single utility class as static methods _(`IOUtils`)_
    * Use proper primitives _(`Integer -> int`)_
    * Use inferable generics where possible  _(`new ArrayList<Type>() -> new ArrayList<>()`)_
    * Use `final` access flag where possible
    * Use [Lombok](https://projectlombok.org/) to simplify bean classes _(Compiler-generated getter/setter/etc)_
    * Move bean classes to separate package
    * Retake bean extends Quiz bean due to shared fields & to allow logical simplifications
    * Add streaming support to `Quizzes` and `Retakes`
    * Add `DataWrapper` for easily shipping around all the course-related beans in one object
* Implement new UI
    * Replace `QuizSchedule` servlet class with JavaFX desktop-application front-end
        * Prompts for class-ID
        * Transitions into `WeekView` when valid class-ID provided
    * Create `WeekView`
        * UI that shows times of the day _(range customizable as constants in class)_ for every day of the week
        * Quiz and retake sessions displayed in parallel
        * Form inputs shown at the top
    * Create `QuizSlot`
        * Element that populates the grid entries of `WeekView`
        * Can hold either a `QuizBean` or `RetakeBean`