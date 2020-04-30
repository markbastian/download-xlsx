# download-xlsx

I'm trying to understand why, when running this app, you can correctly download an Excel file using a direct link (http://localhost:3000/download) but the link provided by the Swagger UI doesn't work. The Swagger UI linked file gets expanded or something (I think it treats the file as an archive and unzips it or maybe does some extra encoding that it shouldn't).

To run it:

 * Load project.clj as lein project
 * Load deps.edn as Clojure tools deps project
 * Execute `clj -m run-jetty` to just run it. Will start the app and launch in a browser.