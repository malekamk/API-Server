function lookupWord() {
    const form = document.getElementById("lookup-form");
    form.addEventListener("submit", (event) => {
        event.preventDefault();

        const data = new FormData(event.target);
        const word = data.get("lookup");

        const options = {
            method: 'GET',
        };

        document.getElementById('results').innerHTML = `<p>Searching for <em>${word}'</em>...</p>`;

        fetch(`https://api.dictionaryapi.dev/api/v2/entries/en/${word}`, options)
            .then(response => response.json())
            .then(data => {
                data = {
                    word: data[0].word,
                    phonetic: data[0].phonetic,
                    partOfSpeech: data[0].meanings[0].partOfSpeech,
                    definitions: data[0].meanings[0].definitions,
                    example: data[0].meanings[0].definitions,
                    verb: data[0].meanings[1] == undefined ? "" : data[0].meanings[1].partOfSpeech

                };
                const template = document.getElementById('results-template').innerText;
                const compiledFunction = Handlebars.compile(template);
                document.getElementById('results').innerHTML = compiledFunction(data);

            });
    });;
}

function lookupSynonyms() {
    const form = document.getElementById("lookup-form");
    form.addEventListener("submit", (event) => {
        event.preventDefault();

        const data = new FormData(event.target);
        const word = data.get("lookup");

        const options = {
            method: 'GET',
        };

        document.getElementById('results').innerHTML = `<p>Searching Synonyms for <em>${word}'</em>...</p>`;

        fetch(`https://api.dictionaryapi.dev/api/v2/entries/en/${word}`, options)
            .then(response => response.json())
            .then(data => {
                data = {
                    word: data[0].word,
                    synonyms: data[0].meanings[0].definitions[0].synonyms // Getting synonyms array
                };

                const synonym_template = document.getElementById('synonym-result-template').innerText;
                const compiledFunction = Handlebars.compile(synonym_template);
                document.getElementById('results').innerHTML = compiledFunction(data);
            });
    });;
}
function lookupAntonyms() {
    const form = document.getElementById("lookup-form");
    form.addEventListener("submit", (event) => {
        event.preventDefault();

        const data = new FormData(event.target);
        const word = data.get("lookup");

        const options = {
            method: 'GET',
        };

        document.getElementById('results').innerHTML = `<p>Searching Antonyms for <em>${word}'</em>...</p>`;

        fetch(`https://api.dictionaryapi.dev/api/v2/entries/en/${word}`, options)
            .then(response => response.json())
            .then(data => {
                data = {
                    word: data[0].word,
                    antonyms: data[0].meanings[0].definitions[0].antonyms
                };

                const antonyms_template = document.getElementById('antonyms-result-template').innerText;
                const compiledFunction = Handlebars.compile(antonyms_template);
                document.getElementById('results').innerHTML = compiledFunction(data);
            });
    });;
}

// tag::router[]
window.addEventListener('DOMContentLoaded', () => {
  const app = $('#app');

  const defaultTemplate = Handlebars.compile($('#default-template').html());
  const dictionaryTemplate = Handlebars.compile($('#dictionary-template').html());
  const antonyms_Template = Handlebars.compile($('#dictionary-template').html());
  const synonyms_Template = Handlebars.compile($('#dictionary-template').html());

  const router = new Router({
    mode:'hash',
    root:'index.html',
    page404: (path) => {
      const html = defaultTemplate();
      app.html(html);
    }
  });

  router.add('/dictionary', async () => {
    html = dictionaryTemplate();
    app.html(html);
    lookupWord();
  });

  router.add('/synonyms', async () => {
    html = synonyms_Template();
    app.html(html);
    lookupSynonyms();
  });

  router.add('/antonyms', async () => {
    html = synonyms_Template();
    app.html(html);
    lookupSynonyms();
  });

  router.addUriListener();

  $('a').on('click', (event) => {
    event.preventDefault();
    const target = $(event.target);
    const href = target.attr('href');
    const path = href.substring(href.lastIndexOf('/'));
    router.navigateTo(path);
  });

  router.navigateTo('/');
});
// end::router[]