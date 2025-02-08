(ns build
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.build.api :as b] ; for b/git-count-revs
            [org.corfield.build :as bb]))
;;(def version (format "0.2.%s" (b/git-count-revs nil)))
;;(def version (format "0.2.%s" (b/git-count-revs nil)))

(def version "0.2")

(def lib 'org.scicloj/scicloj.ml.fasttext)
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))


(defn- pom-template [version]
  [[:description "fasttext model for metamorph.ml and scicloj.ml"]
   [:url "https://github.com/scicloj/scicloj.ml.fasttext"]
   [:licenses
    [:license
     [:name "Eclipse Public License"]
     [:url "http://www.eclipse.org/legal/epl-v10.html"]]]
   [:developers
    [:developer
     [:name "Carsten Behring"]]]
   [:scm
    [:url "https://github.com/scicloj/scicloj.ml.fasttext"]
    [:connection "scm:git:https://github.com/scicloj/scicloj.ml.fasttext"]
    [:developerConnection "scm:git:git@github.com:scicloj/scicloj.ml.fasttext.git"]

    [:tag (str version)]]])

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :pom-data (pom-template version)
                :src-dirs ["src"]})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn test "Run the tests." [opts]
  (bb/run-tests opts))

(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (-> opts
      (assoc :lib lib :version version :aliases [:test-runner])
      (bb/run-tests)
      (bb/clean)
      (jar)))


(defn install "Install the JAR locally." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (jar)
      (bb/install)))


 (defn deploy "Deploy the JAR to Clojars." [opts]
      (-> opts
          (jar)
          (assoc :lib lib :version version)
          
          (bb/deploy)))
