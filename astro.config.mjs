import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

export default defineConfig({
  site: 'https://justquazy.github.io',
  base: '/Hearthstone-SMP-Public',
  integrations: [
    starlight({
      title: 'Hearthstone SMP',
      locales: { root: { label: 'Nederlands', lang: 'nl' } },
      pagefind: false,
      sidebar: [
        { slug: 'index' },
        { slug: 'installeren' },
        { slug: 'problemen-melden' },
      ],
    }),
  ],
});
