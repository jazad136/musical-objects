# musical-objects
Processes Musical Objects Sheet Music Scores to encode and play multi-track midi music through speakers

#### But how? 

It all starts with a note. |o|.

## Sample notation you can feed to musical objects: 

A1 A2 A3 A4 A5 A6 A7 A8 
- which stands for A '1', and A '2', '3', '4', ... on the piano 
B1 B2 B3 B4 B5 B6 B7 B8 
- which stands for B '1', and B '2', '3', '4', ... on the piano 
C1 C2 C3 C4 C5 C6 C7 C8
...
As4 As5 As6 As7 As8 - which stands for "A sharp 4" "A sharp 5" ...  
Bf4 Bf5 Bf6 Bf7 Bf8 - which stands for "B flat 4" "B flat 5" ... 
(A sharp 4 and B flat 4 are equivalent notes on the piano) 

Cs4 Cs5 Cs6 Cs7 Cs8 - which stand for "C sharp 4" and so on
Df4 Df5 Df6 Df7 Df8 - which stand for "D flat 4" "D flat 5" ...
...

With these notes we can add counts to create timed musical pieces that rely on the standard
timings musicians use to practice. 

           // MA-RY  HAD A     LI-TTLE  LAMB
    Line1:    e.q d.q c.q d.q , e.q e.q  e.h
      
           // LI-TTLE LAMB      LI-TTLE  LAMB
    Line2:    d.q d.q  d.h    , e.q g.q  g.h 
 
The output of adding lines like those above to MusicalObjects, allows the built in 
synthesizer to play these notes properly timed to the speakers. 

    //      HEY   JUDE,     DON'T MAKE  IT    BAD...  
       r.q, Ch.q  A.h   r.e A.e,  Ch.e, Dh.e, G.h, r.q

To make things simpler, you might find it easier to use "intervals" in a "key" of a certain signature. We support those as well. 

For starters, this notation can be used to make simple songs. 

Quarter Notes: 
    Q (or q)
Half Notes: 
    H (or h) 
Whole Notes: 
    W (or w)
Sixteenth Notes: 
    S (or s) 
... 
and so on up to 'longa whole' notes (4 x 1 whole note - W4), 
and down to 128th notes (1/132 x 1 quarter note - HTE) 
or rests... Starting a count off with an R or r, makes the marking a "rest", for proper breaks. 

More on intervals, such as using 1, 3, 5, and Keys such as CM in another update to this documentation. 
