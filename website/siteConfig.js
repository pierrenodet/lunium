const repoUrl = "https://github.com/pierrenodet/lunium";

const apiUrl = "/lunium/api/lunium/index.html";

const siteConfig = {
  title: 'lunium',
  tagline: 'Tagless and bifunctor based library for WebDrivers',
  url: 'https://pierrenodet.github.io/lunium',
  baseUrl: '/lunium/',

  projectName: "lunium",
  organizationName: "pierrenodet",

  customDocsPath: "modules/lunium-docs/target/mdoc",

  headerLinks: [
    { href: apiUrl, label: "API Docs" },
    { doc: "overview", label: "Documentation" },
    { href: repoUrl, label: "GitHub" }
  ],

  headerIcon: "img/umbreon.png",
  titleIcon: "img/umbreon.png",
  favicon: "img/umbreon.png",

  colors: {
    primaryColor: '#443e8a',
    secondaryColor: '#2f2b60',
  },

  copyright: `Copyright © ${new Date().getFullYear()} Pierre Nodet`,

  highlight: {
    theme: 'github',
  },

  scripts: ['https://buttons.github.io/buttons.js'],

  onPageNav: 'separate',

  separateCss: ["api"],

  cleanUrl: true,

  repoUrl,

  apiUrl
};

module.exports = siteConfig;
