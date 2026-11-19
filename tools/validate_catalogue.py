#!/usr/bin/env python3
"""Checks every address in app/src/main/assets/feed_catalogue.json and prints the ones that fail.

Run it on a computer with internet:   python tools/validate_catalogue.py
Add --fix to drop the failing ones from the file (a copy is kept as feed_catalogue.json.bak).
A feed counts as good when it answers 200 and the start of the reply looks like RSS, Atom or RDF.
"""
import json, sys, shutil, urllib.request, concurrent.futures as cf

PATH = "app/src/main/assets/feed_catalogue.json"
UA = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 Chrome/120 Mobile Safari/537.36"


def check(entry):
    req = urllib.request.Request(entry["url"], headers={"User-Agent": UA, "Accept": "application/rss+xml, application/atom+xml, text/xml, */*"})
    try:
        with urllib.request.urlopen(req, timeout=20) as r:
            head = r.read(4096).decode("utf-8", "ignore").lower()
            if r.status == 200 and any(t in head for t in ("<rss", "<feed", "<rdf:rdf", "<channel")):
                return entry, None
            return entry, "does not look like a feed"
    except Exception as e:  # noqa: BLE001
        return entry, str(e)


def main():
    data = json.load(open(PATH, encoding="utf-8"))
    with cf.ThreadPoolExecutor(8) as pool:
        results = list(pool.map(check, data["feeds"]))
    bad = [(e, why) for e, why in results if why]
    for e, why in bad:
        print("FAIL", e["title"], e["url"], "->", why)
    print(len(results) - len(bad), "ok,", len(bad), "failed")
    if "--fix" in sys.argv and bad:
        shutil.copy(PATH, PATH + ".bak")
        data["feeds"] = [e for e, why in results if not why]
        json.dump(data, open(PATH, "w", encoding="utf-8"), indent=1, ensure_ascii=False)
        print("Removed the failed ones. Backup:", PATH + ".bak")


if __name__ == "__main__":
    main()

# Rebuild trigger: signing secrets added in the fork.
