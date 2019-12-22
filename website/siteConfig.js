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

  headerIcon: "img/noctali.png",
  titleIcon: "img/noctali.png",
  favicon: "img/umbreon.png",

  colors: {
    primaryColor: '#171717',
    secondaryColor: '#3D3D42',
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
