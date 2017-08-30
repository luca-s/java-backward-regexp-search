A Java class for searching regular expressions or simple text forward and backward in a CharSequence
====================================================================================================

Use it like this:

.. code:: java

    Search s = new Search("Big long text here to be searched [...]");
    s.setPattern("some regexp");

    // search backwards or forward as many times as you like,
    // the class keeps track where the last match was
    MatchResult where = s.searchBackward();
    where = s.searchBackward(); // next match
    where = s.searchBackward(); // next match

    //or search forward
    where = s.searchForward();
    where = s.searchForward();

.. image:: https://github.com/luca-s/java-backward-regexp-search/raw/master/exaple.png

Because java regexp doesn't support backward search capabilities, the class has to simulate it. 
To simulate the backward search (that Java class doesn't have) the input data is divided into chunks and each chunk is searched from last to first until a match is found (inter-chunk matches are returned from last to first too).

The search can fail if the pattern/match you look for is longer than the chunk size, but you can set the chunk size to a sensible size depending on the specific application.

Also, because the match could span between two adjacent chunks, the chunks are partially overlapping. Again, this overlapping size should be set to a sensible size.

A typical application where the user search for some words in a document will work perfectly fine with default values. The matches are expected to be between 10-15 chars, so any chunk size and overlapping size bigger than this expected length will be fine.

I used this class in an application where the users can search strings in a long text (like search feature in a Web browser). So it's tested and works well for practical use cases.


