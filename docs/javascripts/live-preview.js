const previewBase = new URL("../preview/", import.meta.url);
const quietStartMs = 1000;
const gcProbe = new Uint8Array([0, 97, 115, 109, 1, 0, 0, 0, 1, 5, 1, 95, 1, 120, 0]);
const exceptionsProbe = new Uint8Array([0, 97, 115, 109, 1, 0, 0, 0, 1, 4, 1, 96, 0, 0, 3, 2, 1, 0, 10, 8, 1, 6, 0, 6, 64, 25, 11, 11]);

function browserSupported() {
  return typeof WebAssembly === "object" &&
    WebAssembly.validate(gcProbe) &&
    WebAssembly.validate(exceptionsProbe);
}

function savesData() {
  const connection = navigator.connection;
  return Boolean(connection && (connection.saveData || /2g/.test(connection.effectiveType)));
}

function currentTheme() {
  return document.body.getAttribute("data-md-color-scheme") === "slate" ? "dark" : "light";
}

function element(tag, className, text) {
  const node = document.createElement(tag);
  node.className = className;
  if (text) {
    node.textContent = text;
  }
  return node;
}

async function download(url, signal, onBytes) {
  const response = await fetch(url, { signal: signal });
  if (!response.ok) {
    throw new Error("HTTP " + response.status);
  }
  const reader = response.body.getReader();
  while (true) {
    const chunk = await reader.read();
    if (chunk.done) {
      return;
    }
    onBytes(chunk.value.length);
  }
}

async function prefetch(signal, onProgress) {
  const manifest = await (await fetch(new URL("manifest.json", previewBase), { signal: signal })).json();
  let loaded = 0;
  await Promise.all(manifest.files.map(function (file) {
    return download(new URL(file, previewBase), signal, function (bytes) {
      loaded += bytes;
      onProgress(Math.min(1, loaded / manifest.bytes));
    });
  }));
}

function start(figure, button, revealLoaderAfterMs) {
  const controller = new AbortController();
  const bar = element("div", "jenga-live__bar");
  const fill = element("div", "jenga-live__fill");
  const status = element("span", "jenga-live__status", "Loading live preview 0%");
  const cancel = element("button", "jenga-live__cancel", "Cancel");
  const loading = element("div", "jenga-live__loading");
  bar.appendChild(fill);
  loading.append(bar, status, cancel);
  loading.style.visibility = "hidden";
  setTimeout(function () {
    loading.style.visibility = "";
  }, revealLoaderAfterMs);
  button.replaceWith(loading);
  figure.classList.add("jenga-live--loading");

  function reset(message) {
    controller.abort();
    figure.classList.remove("jenga-live--loading");
    loading.replaceWith(button);
    button.textContent = message || "Try it live";
  }

  cancel.addEventListener("click", function () {
    reset();
  });

  prefetch(controller.signal, function (fraction) {
    const percent = Math.round(fraction * 100);
    fill.style.width = percent + "%";
    status.textContent = "Loading live preview " + percent + "%";
  }).then(function () {
    status.textContent = "Starting…";
    mount(figure, loading, controller);
  }).catch(function (error) {
    if (error.name !== "AbortError") {
      reset("Couldn't load the live preview. Retry");
    }
  });
}

function mount(figure, loading, controller) {
  const url = new URL("index.html", previewBase);
  url.searchParams.set("component", figure.dataset.component);
  url.searchParams.set("theme", currentTheme());
  const frame = element("iframe", "jenga-live__frame");
  frame.title = "Live preview";
  frame.src = url.href;
  figure.appendChild(frame);

  function onMessage(event) {
    if (event.source !== frame.contentWindow || event.origin !== location.origin) {
      return;
    }
    const match = /^jenga-height:(\d+)$/.exec(String(event.data));
    if (match) {
      const measured = figure.dataset.stage === "overlay" ? figure.dataset.height : match[1];
      frame.style.height = measured + "px";
      figure.classList.replace("jenga-live--loading", "jenga-live--running");
      loading.remove();
    }
  }

  window.addEventListener("message", onMessage);
  controller.signal.addEventListener("abort", function () {
    window.removeEventListener("message", onMessage);
    frame.remove();
  });

  new MutationObserver(function () {
    frame.contentWindow.postMessage("jenga-theme:" + currentTheme(), location.origin);
  }).observe(document.body, { attributes: true, attributeFilter: ["data-md-color-scheme"] });
}

function setUp(figure) {
  const button = element("button", "jenga-live__start", "Try it live");
  button.addEventListener("click", function () {
    start(figure, button, 0);
  });
  if (!browserSupported()) {
    figure.appendChild(element(
      "p",
      "jenga-live__notice",
      "The live preview needs Chrome or Edge 119+, Firefox 120+ or Safari 18.2+.",
    ));
    return;
  }
  figure.appendChild(button);
  if (!savesData()) {
    start(figure, button, quietStartMs);
  }
}

function prefetchForComponentsSection() {
  const inSection = location.pathname.includes("/components/");
  if (inSection && browserSupported() && !savesData()) {
    prefetch(new AbortController().signal, function () {});
  }
}

const figures = document.querySelectorAll(".jenga-live");
if (figures.length > 0) {
  figures.forEach(setUp);
} else {
  window.addEventListener("load", prefetchForComponentsSection);
}
