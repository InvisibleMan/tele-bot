# https://github.com/Quantisan/docker-clojure/blob/master/alpine/lein/Dockerfile

FROM openjdk:8-alpine
LABEL maintainer="Wes Morgan <wesmorgan@icloud.com>"

ENV LEIN_VERSION=2.8.1
ENV LEIN_INSTALL=/usr/local/bin/

WORKDIR /tmp

RUN apk add --update tar gnupg bash openssl python py-pip && rm -rf /var/cache/apk/*

# Download the whole repo as an archive
RUN mkdir -p $LEIN_INSTALL \
  && wget -q https://raw.githubusercontent.com/technomancy/leiningen/$LEIN_VERSION/bin/lein-pkg \
  && echo "Comparing lein-pkg checksum ..." \
  && echo "019faa5f91a463bf9742c3634ee32fb3db8c47f0 *lein-pkg" | sha1sum -c - \
  && mv lein-pkg $LEIN_INSTALL/lein \
  && chmod 0755 $LEIN_INSTALL/lein \
  && wget -q https://github.com/technomancy/leiningen/releases/download/$LEIN_VERSION/leiningen-$LEIN_VERSION-standalone.zip \
  && wget -q https://github.com/technomancy/leiningen/releases/download/$LEIN_VERSION/leiningen-$LEIN_VERSION-standalone.zip.asc \
  && gpg --keyserver pool.sks-keyservers.net --recv-key 2B72BF956E23DE5E830D50F6002AF007D1A7CC18 \
  && echo "Verifying Jar file signature ..." \
  && gpg --verify leiningen-$LEIN_VERSION-standalone.zip.asc \
  && rm leiningen-$LEIN_VERSION-standalone.zip.asc \
  && mkdir -p /usr/share/java \
  && mv leiningen-$LEIN_VERSION-standalone.zip /usr/share/java/leiningen-$LEIN_VERSION-standalone.jar \
  && echo "\n INSTALL youtube-dl" \
  && wget https://yt-dl.org/downloads/latest/youtube-dl -O /usr/local/bin/youtube-dl \
  && chmod a+rx /usr/local/bin/youtube-dl

ENV PATH=$PATH:$LEIN_INSTALL
ENV LEIN_ROOT 1

WORKDIR /usr/src/app

COPY project.clj /usr/src/app/

RUN lein deps
COPY . /usr/src/app

ENV BOT_SAVE_PATH=/var/data/downloads
RUN mkdir -p $BOT_SAVE_PATH

RUN lein uberjar

ENTRYPOINT ["./entrypoint.sh"]