import gzip
import json
import shutil
import sys
from pathlib import Path

ALLOWED_GROWTH = 1.10


def copy_bundle(source, target):
    shutil.rmtree(target, ignore_errors=True)
    shutil.copytree(
        source,
        target,
        ignore=shutil.ignore_patterns("*.map", "*.LICENSE.txt"),
    )


def write_manifest(target):
    files = sorted(path.name for path in target.glob("*.wasm")) + ["catalog.js"]
    manifest = {
        "files": files,
        "bytes": sum((target / name).stat().st_size for name in files),
    }
    (target / "manifest.json").write_text(json.dumps(manifest))


def first_load_gzip_bytes(target):
    first_load = list(target.glob("*.wasm")) + [target / "catalog.js"] + list(target.rglob("*.ttf"))
    return sum(len(gzip.compress(path.read_bytes())) for path in first_load)


def main():
    source, target, baseline_file = (Path(argument) for argument in sys.argv[1:4])
    copy_bundle(source, target)
    write_manifest(target)
    size = first_load_gzip_bytes(target)
    baseline = int(baseline_file.read_text().strip())
    limit = int(baseline * ALLOWED_GROWTH)
    print(f"Live preview first load: {size} bytes gzip (baseline {baseline}, limit {limit})")
    if size > limit:
        sys.exit(f"Live preview grew more than 10% over {baseline_file}; update it only on purpose.")


main()
