(ns bio.sam.core
  (:use [clojure.contrib.io :only (file)])
  (:import [net.sf.samtools SAMFileReader SAMRecordIterator]))

(defn read-sam [filename]
  "Read file in SAM/BAM format and return a lazy sequence of
   SAMRecord objects. Close the file when the last record has
   been read"
  (let [sam-reader (SAMFileReader. (file filename))
	read-record (fn this [#^SAMRecordIterator it]
		      (lazy-seq
		       (if (.hasNext it)
			 (cons (.next it) (this it))
			 (do (.close it) (.close sam-reader)))))]
    (read-record (.iterator sam-reader))))
    
(comment

  (def s (read-sam "examples/toy.sam"))
  ;;=> #'user/s

  (first s)
  ;;=> #<SAMRecord r001 2/2 17b aligned read.>

  (.getCigarString (first s))
  ;;=> "8M2I4M1D3M"
  
  (map (memfn getCigarString) s)
  ;;=> ("8M2I4M1D3M" "1S2I6M1P1I4M2I" "5H6M" "6M14N1I5M" "6H5M" "9M")
  
  (.getReadLength (first s))
  ;;=> 17

  (map (memfn getReadLength) s)
  ;;=> (17 16 6 12 5 9)

  (map (memfn getReadString) s)
  ;;=> ("TTAGATAAAGGATACTG" "AAAAGATAAGGATAAA" "AGCTAA" "ATAGCTCTCAGC" "TAGGC" "CAGCGCCAT")
  
  ;; See <http://picard.sourceforge.net/javadoc/net/sf/samtools/SAMRecord.html>
  ;; for other methods supported by SAMRecord
  
  )