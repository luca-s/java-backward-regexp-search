import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Search regular expressions or simple text forward and backward in a CharSequence
 *
 * 
 * To simulate the backward search (that Java class doesn't have) the input data
 * is divided into chunks and each chunk is searched from last to first until a 
 * match is found (inter-chunk matches are returned from last to first too).
 *
 * The search can fail if the pattern/match you look for is longer than the chunk
 * size, but you can set the chunk size to a sensible size depending on the specific
 * application.
 *
 * Also, because the match could span between two adjacent chunks, the chunks are
 * partially overlapping. Again, this overlapping size should be set to a sensible
 * size.
 *
 * A typical application where the user search for some words in a document will
 * work perfectly fine with default values. The matches are expected to be between
 * 10-15 chars, so any chunk size and overlapping size bigger than this expected 
 * length will be fine.
 *
 * */
public class Search {

    private int BACKWARD_BLOCK_SIZE = 200;
    private int BACKWARD_OVERLAPPING = 20;

    private Matcher myFwdMatcher;
    private Matcher myBkwMatcher;
    private String  mySearchPattern;
    private int myCurrOffset;
    private boolean myRegexp;
    private CharSequence mySearchData;

    public Search(CharSequence searchData) {
        mySearchData = searchData;
        mySearchPattern = "";
        myCurrOffset = 0;
        myRegexp = true;
        clear();
    }

    public void clear() {
        myFwdMatcher = null;
        myBkwMatcher = null;
    }

    public String getPattern() {
        return mySearchPattern;
    }

    public void setPattern(String toSearch) {
        if ( !mySearchPattern.equals(toSearch) ) {
            mySearchPattern = toSearch;
            clear();
        }
    }

    public CharSequence getText() {
        return mySearchData;
    }
    
    public void setText(CharSequence searchData) {
        mySearchData = searchData;
        clear();
    }

    public void setSearchOffset(int startOffset) {
        if (myCurrOffset != startOffset) {
            myCurrOffset = startOffset;
            clear();
        }
    }

    public boolean isRegexp() {
        return myRegexp;
    }

    public void setRegexp(boolean regexp) {
        if (myRegexp != regexp) {
            myRegexp = regexp;
            clear();
        }
    }

    public MatchResult searchForward() {

        if (mySearchData != null) {

            boolean found;

            if (myFwdMatcher == null)
            {
                // if it's a new search, start from beginning
                String searchPattern = myRegexp ? mySearchPattern : Pattern.quote(mySearchPattern);
                myFwdMatcher = Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE).matcher(mySearchData);
                try {
                    found = myFwdMatcher.find(myCurrOffset);
                } catch (IndexOutOfBoundsException e) {
                    found = false;
                }
            }
            else
            {
                // continue searching
                found = myFwdMatcher.hitEnd() ? false : myFwdMatcher.find();
            }

            if (found) {
                MatchResult result = myFwdMatcher.toMatchResult();
                return onMatchResult(result);
            }
        }
        return onMatchResult(null);
    }

    public MatchResult searchBackward() {

        if (mySearchData != null) {

            myFwdMatcher = null;

            if (myBkwMatcher == null)
            {
                // if it's a new search, create a new matcher
                String searchPattern = myRegexp ? mySearchPattern : Pattern.quote(mySearchPattern);
                myBkwMatcher = Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE).matcher(mySearchData);
            }

            MatchResult result = null;
            boolean startOfInput = false;
            int start = myCurrOffset;
            int end = start;

            while (result == null && !startOfInput)
            {
                start -= BACKWARD_BLOCK_SIZE;
                if (start < 0) {
                    start = 0;
                    startOfInput = true;
                }
                try {
                    myBkwMatcher.region(start, end);
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                while ( myBkwMatcher.find() ) {
                    result = myBkwMatcher.toMatchResult();
                }
                end = start + BACKWARD_OVERLAPPING; // depending on the size of the pattern this could not be enough
                                                    //but how can you know the size of a regexp match beforehand?
            }

            return onMatchResult(result);
        }
        return onMatchResult(null);
    }

    private MatchResult onMatchResult(MatchResult result) {
        if (result != null) {
            myCurrOffset = result.start();    
        }
        return result;
    }
}
