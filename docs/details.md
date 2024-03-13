# Details for P4 Autocomplete

Many parts of the assignment, including workflow and the classes you use and write, are part of this document.

## Starter Code and Using Git
**_You should have installed all software (Java, Git, VS Code) before completing this project._** You can find the [directions for installation here](https://coursework.cs.duke.edu/cs-201-spring-24/resources-201/-/blob/main/installingSoftware.md) (including workarounds for submitting without Git if needed).

We'll be using Git and the installation of GitLab at [coursework.cs.duke.edu](https://coursework.cs.duke.edu). All code for classwork will be kept here. Git is software used for version control, and GitLab is an online repository to store code in the cloud using Git.

**[This document details the workflow](https://coursework.cs.duke.edu/cs-201-spring-24/resources-201/-/blob/main/projectWorkflow.md) for downloading the starter code for the project, updating your code on coursework using Git, and ultimately submitting to Gradescope for autograding.** We recommend that you read and follow the directions carefully this first time working on a project! While coding, we recommend that you periodically (perhaps when completing a method or small section) push your changes as explained in below.

## Project Background and Concepts

Autocomplete is an algorithm used in many modern software applications. In all of these applications, the user types text and the application suggests possible completions for that text as shown in the example images below taken from google search.

The left/first was taken in March 2019, the right/second on October 9, 2020)
<div align="center">
  <img width="384" height="344 "src="p4-figures/googleSearch.png">
  <img width="384" height="345" src="p4-figures/googleSearch2.png">
</div>


Although finding terms that contain a query by searching through all possible results is possible, these applications need some way to select only the most useful terms to display (since users will likely not comb through thousands of terms, nor will obscure terms like "duke cookiemonster" be useful to most users). Thus, autocomplete algorithms not only need a way to find terms that start with or contain the prefix, but a way of determining how likely each one is to be useful to the user and displaying "good" terms first. This all needs to be done efficiently so that a user can see completions in real time.

In this project, you will leverage a `Comparator` in Java as well as the binary search algorithm on sorted data to implement an efficient autocompleter. You will create a second implementation based on a `HashMap`. You will then benchmark and analyze the tradeoffs of these implementations.  

According to one study, in order to be useful the algorithm must do all this in less than 100 milliseconds (see article linked below). If it takes any longer, the user will already be inputting the next keystroke (while humans do not on average input one keystroke every 50 milliseconds, additional time is required for server communication, input delay, and other processes). Furthermore, the server must be able to run this computation for every keystroke, for every user. In this assignment, you will be implementing autocomplete using three different algorithms and data structures. Your autocomplete will be different than the industrial examples described above in two ways:

1. Each term will have a predetermined, constant weight/likelihood, whereas actual autocomplete algorithms might change a term's likelihood based on previous searches.
2. We will only consider terms which start with the user query, whereas actual autocomplete algorithms (such as the web browser example above) might consider terms which contain but do not start with the query.

The article linked below describes one group's recent analysis of different data structures to implement autocomplete efficiently. You'll be implementing a version of what they call a prefix hash tree, though we'll use a prefix hash list which is more efficient when terms aren't updated dynamically.
- https://medium.com/@prefixyteam/how-we-built-prefixy-a-scalable-prefix-search-service-for-powering-autocomplete-c20f98e2eff1

### Acknowledgements
The assignment was developed by Kevin Wayne and Matthew Drabick at Princeton University for their Computer Science 226 class. Former head CompSci 201 UTAs, Arun Ganesh (Trinity '17) and Austin Lu (Trinity '15) adapted the assignment for Duke with help from Jeff Forbes. Josh Hug updated the assignment and provided more of the testing framework.

## Classes and Method Details for Code 

### PrefixComparator

You must use only the first `myPrefixSize` characters of the words stored in `Term` objects `v` and `w` that are passed to `PrefixComparator.compare`. However, if the length of either word is less than `myPrefixSize`, this comparator only compares up ***until the end of the shorter word.*** This means that although `"beeswax"` is greater than `"beekeeper"` when compared lexicographically, i.e., with the natural order for strings, the two words are considered equal using a `PrefixComparator.getComparator(3)` since only the first three characters are compared. You can expand below for more details and examples.

#### More details on PrefixComparator

For a `PrefixComparator.getComparator(4)`, `"beeswax"` is greater than `"beekeeper"` since `"bees"` is greater than `"beek"`. But `"bee"` is less than `"beekeeper"` and `"beeswax"` since only the first three characters are compared --- since `"bee"` has only three characters and these three characters are the same. ***The length*** of `"bee"` ***makes it less than*** `"beekeeper"`, just as it is when eight characters are used to compare these words.


***Your code should examine only as many characters as needed to return a value.*** You should examine this minimal number of characters using a loop and calling `.charAt` to examine characters--- you'll need to write your loop and comparisons carefully to ensure that the prefix size is used correctly. See the table below for some examples. Recall that you can subtract characters, so `'a'` - `'b'` is a negative value and `'z'` - `'a'` is a positive value. You can also use `<` and `>` with `char` values.

Here is a reference table for the `PrefixComparator` comparator. 

|r/prefix|v| |w| Note |
|    -   |-|-|-| -    |
|   4  |bee|<|beekeeper|"bee" < "beek"|
|4|bees|>|beek|‘s’ > ‘k’|
|4|bug|>|beeswax|‘u’ > ‘e’|
|4|bees|=|beeswax|"bees" == "bees"|
|3|beekeeper|=|beeswax|"bee" == "bee"|
|3|bee|=|beeswax|"bee" == "bee"|

### BinarySearchLibrary firstIndex and lastIndex

You're given code in `BinarySearchLibrary.firstIndexSlow` that is correct, ***but does not meet performance requirements***. This slow implementation will be very slow in some situations, e.g., when a list has many equal values according to the given comparator. The code in the slow method  is **O(*N*)** where there are *N* equal values since the code could examine all the values. To meet performance criteria your code should be **O(log *N*)**, more specifically it should only need $`1 + \lceil log_2N \rceil`$ comparisons -- that is, one more than $`log_2N`$ rounded up.

To get started, expand below to see an example of the standard Java API  [`Collections.binarySearch`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Collections.html#binarySearch(java.util.List,T,java.util.Comparator)) method that has been slightly changed to use the same parameters as `firstIndex`.

```java
public static <T> int binarySearch(List<T> list, T target,
                                   Comparator<T> comp) {
    int low = 0;
    int high = list.size()-1;
    while (low <= high) {
        int mid = (low + high)/2;
        T midval = list.get(mid);
        int cmp = comp.compare(midval,target);

        if (cmp < 0)
            low = mid + 1;
        else if (cmp > 0)
            high = mid - 1;
        else
            return mid; // target found
     }
     return -1;  // target not found
}
```

This method meets the *performance* requirement and returns an index `i` such that `comp.compare(list.get(i), target)==0`. However, it does *not* guarantee to return the first or last such index `i`. **Your task is to adapt this approach (outlined in the explanation below and in code you get with the project (to copy)) so that `firstIndex` and `lastIndex` return the first and last such indices respectively, while maintaining the same performance guarantee.** 

At a high level, note that binary search is efficient because at each iteration of the `while` loop it reduces the effective search range (`high`-`low`) by a multiplicative factor of 2, leading to the **O(log *N*)** performance. It is also correct because of the following *loop invariant* - at the start of the loop, the target is always at an index between `low` and `high` (if it is in the list). Your algorithm will need to do this as well. However, the example code shown above `return`s as soon as it finds a match. You will need to change this so that your algorithm keeps searching to find the first or last match respectively.

To do this we strongly, strongly, strongly suggest using the loop invariant explained below.


### Implementing method firstIndex (and lastIndex)

In many implementations of binary search, variables `low` and `high` are used to bracket/delimit the range of possible values for the search target -- as is the case in the code shown above. 

### Invariant for `firstIndex`

Instead of using the initialization of low and high shown above, ***you should initialize these values to establish the following loop invariant*** -- an expression that will be true every time the loop test is evaluated, that is both before the loop executes the first time and at the end of every loop iteration.

**(low, high] is interval containing  target, if target is in the list. **

Notice that this is an **open interval on the left**, in particular this means that **`list.get(low)` cannot be equal to target.** To establish this invariant before the loop executes the first time you should consider that ***the open interval `(-1,list.length()-1]` which must contain target if it is present since this interval represents every possible index in `list`.*** Since the interval is open on the left, you cannot initialize low to zero since `(0,99]` includes 99, but not zero! So for a 100-element array you'd set `low=-1` and `high=99`, so the interval `(-1,99]` ensures that if target is present, it's either `list.get(0)` or `list.get(1)` or … `list.get(99)`.

After calculating the midpoint (see reference code above), you'll need to ***re-establish the invariant by comparing the target to the middle value.*** In particular, in the while loop, if you determine (conceptually)

`list[mid] < target`

Then you can set **low to mid** since you know that before the loop `(low,high]` was the interval and if `list[mid] < target` then `(mid,high]` still maintains the invariant.

Otherwise, meaning `list[mid] >= target`, then you can set `high` to `mid` since if before the loop `(low,high]` was the interval and `list[mid] >= target`, then `(low,mid]` still maintains the invariant. Note that this is `>=`, but using a Comparator this means the value of the comparator will be greater than or equal to zero.

### Loop Termination

If `low` and `high` differ by 1 in the interval `(low,high]`, and the list is sorted, then `list.get(high)` is target and `high` is the lowest/first index (if the interval isn't empty) since the interval contains a single value. This means you should use a loop guard/test that loops 

`while (low+1 != high)`

since you know that `low <= high` is always true. Thus, when the loop exits, you'll know that `low == high-1` and the interval `(low,high]` is the same as `(high-1,high]` -- **which contains a single value** -- the index whose value is `high`. 

You can determine whether to return -1 (target not present) based on the value of `high` and the value of `list.get(high)`. For example, if `high` is not a valid index, the interval is empty. ***After the loop you'll need to make one more comparison*** of `a.get(high)` with `target` to see if they are equal. ***This loop will be correct and will meet the performance bounds if you develop it using the invariant.***

You'll find  **nearly the exact code you need for firstIndex** in the method `firstMatch` of the class in`BinaryBenchmark.java`. You should likely copy this code, modify it accordingly/test it, and then read the explanation for `lastIndex`. In particular, the `firstMatch` method only works for lists of Strings (instead of lists of any generic type `T`), so the call to `.compareTo` in `firstMatch` on line 80 of `BinaryBenchmark.java` must be modified to use `comp.compare` with the correct two parameters instead. (String implements `Comparable` but `T` may not, so `.compareTo` may not be implemented for the objects of type `T` in the list for `firstIndex`.)

### Code for `lastIndex`

You should develop a similar invariant and loop for the method `lastIndex` that you'll implement. In this case, the interval you'll consider is `[low,high)`, i.e., **open on the right**. Establish the invariant before the loop, and reason about how to assign to `low` or `high` depending on how the middle value compares to key. You'll initialize `low = 0` before the loop since the interval is open on the left. You'll need to think about resetting `low` or `high` based on the `Comparator` value. Since the interval is open on the right, when resetting `high` it you'll know the value being searched for cannot have index `high`. You'll need to test the actual value of the list at index `low`, if that's a valid index, the same way you checked
the value at index `high` in the code for `firstIndex`.


### Testing BinarySearchLibrary Methods

You're given two classes to help verify that your methods are correct and meet performance requirements. The JUnit tests in `TestBinarySearchLibrary` can help you verify that your methods return the correct values. The output of running `BinaryBenchmark` can help verify both ***correctness and performance requirements.*** The output using a correct `BinarySearchLibrary` class is shown below when running `BinaryBenchmark` (note,the code is actually in the `firstMatch` method you'll copy to `BinarySearchLibrary`). The values in both `index` columns should be the same: the location of the first occurrence of the target `banana`. The `scomps` column is the number of comparisons made by the slow implementation `firstIndexSlow`. The `fcomps` column is the number of comparisons made by code you'll move to `firstIndex`. You can modify to test for `lastIndex` to see if it's similar.
```
    size. sdex  scomps fdex fcomps
    1024	0	644	    0	13
    2048	0	1297	0	14
    4096	0	2578	0	15
    8192	0	5139	0	16
   16384	0	10260	0	17
   32768	0	20501	0	18
```

### BinarySearchAutocomplete topMatches

The `topMatches` method requires that you return the weightiest `k` matches that match `prefix` that's a parameter to `topMatches` --- note that `k` is a parameter to the method as well -- in order of weight. The calls to `firstIndex` and `lastIndex` give the first and last indices of `myTerms` that match. The code you write will need to return the `k` greatest `weight` of these in order. If there are fewer than `k` matches, it should just return all of the matches in order. See below for more details on how to do this efficiently using a `PriorityQueue`. 

The binary search in the `firstIndex` and `lastIndex` methods are both `O(log N)`. Then, if there are `M` terms that match the prefix, the simple method of finding the `M` matches, copying them to a list, sorting them in reverse weight order, and then choosing the first `k` of them will run in the total of the times given below. Using this approach will thus have complexity/performance of `O(log N + M log M)`. 

|Complexity|Reason|
| ---      |  ---  |
|O(log N)|Call firstIndex and lastIndex|
|O(M log(M))|Sort all M elements that match prefix|
|O(k)|Return list of top k matches|

It's quite possible that `k < M`, and often `k` will be *much* less than `M`. Rather than sorting all `M` entries that match the prefix, you can use a size-limited priority queue **using the same idea and code that's used in** the `topMatches` method from `BruteAutocomplete`. Reference the code there for ideas. ***This is the approach you should implement.***

This should make `topMatches` run in `O(log N + M log k)` time instead of `O(log N + M log M)`. In benchmarking, there may not be a noticeable difference for the data files you're given for small values of `M`, though with larger values of `M` there will be a difference.

|Complexity|Reason|
| ---      |  ---  |
|O(log N)|Call firstIndex and lastIndex|
|O(M log(k))|Keep best k elements in priority queue|
|O(k log(k))|Return list of top k matches, removing one at a time from priority queue|


You're given a JUnit test class `TestBinarySearchAutocomplete` that you should run to verify your methods work correctly. You should also change the code in `AutocompleteMain` to use the `BinarySearchAutocomplete` class -- see the commented out lines as shown below. Then be sure that the output matches what you saw earlier when running the `main` method using `BruteAutocomplete`.

```java
final static String AUTOCOMPLETOR_CLASS_NAME = BRUTE_AUTOCOMPLETE;
//final static String AUTOCOMPLETOR_CLASS_NAME = BINARY_SEARCH_AUTOCOMPLETE;
//final static String AUTOCOMPLETOR_CLASS_NAME = HASHLIST_AUTOCOMPLETE;
```

## HashListAutocomplete

The declaration of the class and the instance variables you will need to add are shown in the code below. 

```java
public class HashListAutocomplete implements Autocompletor {

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;
}
```

There are four methods you will need to implement, stubs of which are provided in the starter code: `initialize`, `topMatches`, and `sizeInBytes` (details below).

The class should maintain a `HashMap` of _every possible prefix_ (for each term) (up to the number of characters specified by a constant `MAX_PREFIX` that you should set to 10 as shown. The key in the map is a prefix/substring. The value for each prefix key is a weight-sorted list of `Term` objects that share that prefix. The diagram below shows part of such a `HashMap`. Three prefixes are shown---the corresponding values are shown as a weight-sorted list of `Term` objects.

|Prefix|Term Objects|
| --   |    ----    |
|"ch"| ("chat",50), ("chomp",40), ("champ",30), ("chocolate",10)|
|"cho"|("chomp",40), ("chocolate",10)|
|"cha | ("chat",50), ("champ", 30)|

Details on the four specific methods you need to write are in the sections below.

### HashListAutocomplete constructor

You should create a constructor similar to those in the other implementations like `BruteAutocomplete` and `BinarySearchAutocomplete`; look at those for examples. The constructor should check for invalid conditions and throws exceptions in those cases, otherwise it should simply call the `initialize()` method passing `terms` and `weights`.

### HashListAutocomplete initialize method

For each `Term` in `initialize`, use the first `MAX_PREFIX` substrings as a key in the map the class maintains and uses. For each prefix you'll store the `Term` objects with that prefix in an `ArrayList` that is the corresponding value for the prefix in the map.

***After*** all keys and values have been entered into the map, you'll write code to sort every value in the map, that is each `ArrayList` corresponding to each prefix. You must use a `Comparator.comparing(Term::getWeight).reversed()` object to sort so that the list is maintained sorted from high to low by weight, e.g., see below, and sort using this idea for each list associated with a key in the map.

While it is not required, we highly recommend updating `mySize` as you're creating the map. The
instance variable `mySize` is a rough estimate of the number of bytes required to create the HashMap (both the String keys and the Term values in the HashMap). For each string you create, you'll need to add to `mySize` the value `BYTES_PER_CHAR * length` as the number of bytes needed. For each term you store, each string stored contributes `BYTES_PER_CHAR * length` and each double stored contributes `BYTES_PER_DOUBLE`. If you follow these instructions, then you can ignore the sizeInBytes instructions and just return `mySize`.

```java
Collections.sort(list, Comparator.comparing(Term::getWeight).reversed())`
```

**_Be sure that you include the empty string as a substring!_** As an example, if I initialize HashListAutocomplete with one Term ("hippopotamus", 40), then my HashMap should be 

|Prefix|Term Objects|
| --   |    ----    |
|""| ("hippopotamus", 40)|
|"h"| ("hippopotamus", 40)|
|"hi"| ("hippopotamus", 40)|
|"hip"| ("hippopotamus", 40)|
|"hipp"| ("hippopotamus", 40)|
|"hippo"| ("hippopotamus", 40)|
|"hippop"| ("hippopotamus", 40)|
|"hippopo"| ("hippopotamus", 40)|
|"hippopot"| ("hippopotamus", 40)|
|"hippopota"| ("hippopotamus", 40)|
|"hippopotam"| ("hippopotamus", 40)|

We stop at "hippopotam" since we take prefixes up to `MAX_PREFIX` length.

### HashListAutocomplete topMatches method

The implementation of `topMatches` can then be done in about five lines of code or fewer. First, check that the `prefix` parameter has at most `MAX_PREFIX` characters, otherwise shorten it by truncating the trailing characters to `MAX_PREFIX` length. 

Then, if `prefix` is in the map, get the corresponding value (a `List` of `Term` objects) and return a sublist of the first `k` entries (or all of the entries if there are fewer than `k`). Otherwise, you should return an empty list. Here's code that can help:

```java
List<Term> all = myMap.get(prefix);
List<Term> list = all.subList(0, Math.min(k, all.size()));
```

### The sizeInBytes method

You'll need to implement the required `sizeInBytes` method. This method should return an estimate of the amount of memory (in bytes) necessary to store all of the keys and values in the `HashMap`. This can be computed once the first time `sizeInBytes` is called (that is, when `mySize == 0`) and stored in the instance variable `mySize`; on subsequent calls it can just return `mySize`. You can see similar examples in the `sizeInBytes` methods of `BruteAutocomplete` and `BinarySearchAutocomplete`.

Your method should account for every `Term` object and every String/key in the map. Use the implementations of `sizeInBytes` in the other `Autocomplete` classes as a model. Each string stored contributes `BYTES_PER_CHAR * length` to the bytes need. Each double stored contributes `BYTES_PER_DOUBLE`. You'll account for every `Term` stored in one of the lists in the map (each consisting of a String and a double) as well as every key (Strings) in the map.




