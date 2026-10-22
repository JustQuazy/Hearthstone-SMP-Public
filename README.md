# Hearthstone SMP player site

This repository publishes the Dutch player handbook and two Packwiz manifests through GitHub Pages.

Astro + Starlight render the handbook. The Pages base matches the repository name, `/Hearthstone-SMP-Public`. The public payload contains two independently hashed Packwiz trees at `public/packs/essentials/` and `public/packs/extended/`. Their URLs are `https://justquazy.github.io/Hearthstone-SMP-Public/packs/essentials/pack.toml` and `https://justquazy.github.io/Hearthstone-SMP-Public/packs/extended/pack.toml`.

The public trees contain only Packwiz manifests, pinned download metadata and configuration files. Voxy and Voxy Extra use pinned Modrinth downloads. Locally supplied resource-pack ZIPs are carried only by separate private Prism ZIPs and remain outside Packwiz update management.

Run `npm ci` and `npm run build` to build the site locally. Pushes to `main` run the Pages deployment workflow in `.github/workflows/pages.yml`.

Search indexing is disabled for this small initial handbook.
