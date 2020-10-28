#!/bin/sh
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg2 --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output=./pipeline.bash ./.github/scripts/pipeline.sh.gpg
